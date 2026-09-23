package id.sch.smkwhapo.cbtexambrowser

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Receiver kosong yang wajib ada supaya aplikasi bisa didaftarkan sebagai
 * Device Admin, lalu (lewat `adb shell dpm set-device-owner`) sebagai
 * Device Owner. Tidak ada kebijakan admin aktif selain yang dideklarasikan
 * di res/xml/device_admin.xml — receiver ini hanya "jangkar" identitas admin
 * yang dipakai DevicePolicyManager untuk mengizinkan setLockTaskPackages().
 */
class CbtDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        fun getComponentName(context: Context): ComponentName =
            ComponentName(context.applicationContext, CbtDeviceAdminReceiver::class.java)

        fun isDeviceOwner(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.isDeviceOwnerApp(context.packageName)
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }
}
