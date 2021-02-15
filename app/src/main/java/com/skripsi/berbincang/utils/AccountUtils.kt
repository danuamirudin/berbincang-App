
package com.skripsi.berbincang.utils

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.skripsi.berbincang.R
import com.skripsi.berbincang.application.NextcloudTalkApplication
import com.skripsi.berbincang.models.ImportAccount
import com.skripsi.berbincang.models.database.UserEntity
import java.util.*

object AccountUtils {

    private val TAG = "AccountUtils"

    fun findAccounts(userEntitiesList: List<UserEntity>): List<Account> {
        val context = NextcloudTalkApplication.sharedApplication!!.applicationContext
        val accMgr = AccountManager.get(context)
        val accounts = accMgr.getAccountsByType(context.getString(R.string.nc_import_account_type))

        val accountsAvailable = ArrayList<Account>()
        var importAccount: ImportAccount
        var internalUserEntity: UserEntity
        var accountFound: Boolean
        for (account in accounts) {
            accountFound = false

            for (i in userEntitiesList.indices) {
                internalUserEntity = userEntitiesList[i]
                importAccount = getInformationFromAccount(account)
                if (importAccount.token != null) {
                    if (importAccount.baseUrl.startsWith("http://") || importAccount.baseUrl.startsWith("https://")) {
                        if (internalUserEntity.username == importAccount.username && internalUserEntity.baseUrl == importAccount.baseUrl) {
                            accountFound = true
                            break
                        }
                    } else {
                        if (internalUserEntity.username == importAccount.username && (internalUserEntity
                                        .baseUrl == "http://" + importAccount.baseUrl ||
                                        internalUserEntity.baseUrl == "https://" + importAccount
                                        .baseUrl)) {
                            accountFound = true
                            break
                        }

                    }
                } else {
                    accountFound = true
                    break
                }
            }

            if (!accountFound) {
                accountsAvailable.add(account)
            }
        }

        return accountsAvailable
    }

    fun getAppNameBasedOnPackage(packageName: String): String {
        val context = NextcloudTalkApplication.sharedApplication!!.applicationContext
        val packageManager = context.packageManager
        var appName = ""
        try {
            appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA)) as String
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Failed to get app name based on package")
        }

        return appName
    }

    fun canWeOpenFilesApp(context: Context, accountName: String): Boolean {
        val pm = context.packageManager
        try {
            val packageInfo = pm.getPackageInfo(context.getString(R.string.nc_import_accounts_from), 0)
            if (packageInfo.versionCode >= 30060151) {
                val ownSignatures = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures
                val filesAppSignatures = pm.getPackageInfo(context.getString(R.string.nc_import_accounts_from), PackageManager.GET_SIGNATURES).signatures

                if (Arrays.equals(ownSignatures, filesAppSignatures)) {
                    val accMgr = AccountManager.get(context)
                    val accounts = accMgr.getAccountsByType(context.getString(R.string.nc_import_account_type))
                    for (account in accounts) {
                        if (account.name == accountName) {
                            return true
                        }
                    }
                } else {
                    return true
                }
            }
        } catch (appNotFoundException: PackageManager.NameNotFoundException) {

        }

        return false
    }

    fun getInformationFromAccount(account: Account): ImportAccount {
        val lastAtPos = account.name.lastIndexOf("@")
        var urlString = account.name.substring(lastAtPos + 1)
        val username = account.name.substring(0, lastAtPos)

        val context = NextcloudTalkApplication.sharedApplication!!.applicationContext
        val accMgr = AccountManager.get(context)

        var password: String? = null
        try {
            password = accMgr.getPassword(account)
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to import account")
        }

        if (urlString.endsWith("/")) {
            urlString = urlString.substring(0, urlString.length - 1)
        }

        return ImportAccount(username, password, urlString)
    }
}

