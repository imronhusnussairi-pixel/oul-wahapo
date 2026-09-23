# CBT Exam Browser Wahapo

Aplikasi Android WebView yang langsung membuka `https://cbt.smkwhapo.sch.id/`.

## Fitur

- WebView khusus CBT, langsung terarah ke URL ujian
- Fullscreen (status bar & navigation bar disembunyikan)
- Orientasi terkunci **portrait**
- JavaScript aktif
- Cookie/session didukung (WebView menyimpan sesi login)
- **Safe Browsing** Google aktif (memblokir halaman phishing/malware yang dikenal)
- **HTTPS wajib** — navigasi ke http:// non-aman atau sertifikat SSL bermasalah otomatis diblokir
- Navigasi dibatasi hanya ke domain `cbt.smkwhapo.sch.id` — link ke domain lain diblokir
- Tombol Back Android: mundur di dalam riwayat WebView dulu; kalau sudah di halaman awal, muncul
  konfirmasi keluar
- **Tombol keluar terlihat** (ikon "X" di pojok kanan atas) dengan dialog konfirmasi sebelum benar-benar
  keluar aplikasi
- Dukungan upload file (`<input type="file">`), untuk unggah jawaban/scan
- Ikon aplikasi placeholder (lihat catatan di bawah)

## ⚠️ Cek dulu sebelum build: nama domain

File ini dibuat memakai domain **`cbt.smkwhapo.sch.id`** (tanpa huruf "a" setelah "smk"), sesuai yang
diminta. Kalau ternyata ini salah ketik dan domain aslinya berbeda (misalnya `smkwahapo`), edit dua baris
ini di `app/src/main/res/values/strings.xml` sebelum build:

```xml
<string name="exam_url">https://cbt.smkwhapo.sch.id/</string>
<string name="allowed_host">cbt.smkwhapo.sch.id</string>
```

## Cara mendapatkan file APK (tanpa Android Studio)

1. Buat repository baru di GitHub (atau pakai yang sudah ada).
2. Upload **seluruh isi** folder `cbt-exam-browser-wahapo` ini ke repo tersebut — termasuk folder
   `.github` yang tersembunyi (aktifkan "Show hidden items" di File Explorer / `Cmd+Shift+.` di Mac
   sebelum drag-drop, atau buat filenya manual lewat "Add file → Create new file" dengan path
   `.github/workflows/build.yml` kalau drag-drop gagal).
3. Buka tab **Actions** di repo → workflow "Build APK" otomatis jalan (±3-5 menit).
4. Setelah selesai (centang hijau ✅), buka run tersebut → scroll ke bagian **Artifacts** →
   download `cbt-exam-browser-wahapo-debug-apk` → ekstrak → dapat `app-debug.apk`.
5. Pindahkan ke HP Android, buka filenya, izinkan instal dari sumber tidak dikenal jika diminta,
   lalu install.

## Mode Device Owner (kiosk tanpa dialog "screen pinning")

Secara default, saat aplikasi masuk mode kiosk (`startLockTask()`), Android menampilkan dialog
konfirmasi "screen pinning" satu kali ke pengguna. Ini bisa dihilangkan dengan menjadikan aplikasi
**Device Owner** di perangkat tersebut. Kode pendukungnya sudah ada di project ini
(`CbtDeviceAdminReceiver.kt`, `res/xml/device_admin.xml`, dan pengecekan di `MainActivity.kt`) —
yang perlu dilakukan hanyalah **mendaftarkan** aplikasi sebagai device owner di tiap HP/tablet ujian.

**Syarat utama:** perangkat harus dalam kondisi "bersih" — belum ada akun Google mana pun yang login
di perangkat itu (device owner tidak bisa didaftarkan kalau sudah ada akun terdaftar). Kalau perangkat
sudah lama dipakai, factory reset dulu.

**Langkah pendaftaran (sekali per perangkat), lewat komputer + kabel USB:**

1. Di HP: aktifkan **Developer Options** (Setelan → Tentang Ponsel → ketuk "Nomor Build" 7 kali),
   lalu aktifkan **USB Debugging** di Developer Options.
2. Install APK aplikasi ini ke HP tersebut (`adb install app-debug.apk`), **tapi jangan dibuka dulu**.
3. Pastikan perangkat belum punya akun Google apa pun (cek Setelan → Akun).
4. Dari komputer (dengan [platform-tools/adb](https://developer.android.com/tools/releases/platform-tools)
   terpasang), sambungkan HP lewat USB dan jalankan:
   ```
   adb devices
   ```
   (pastikan HP terdeteksi dan status "device", bukan "unauthorized" — kalau perlu, konfirmasi dialog
   izin USB debugging di layar HP dulu)
5. Jalankan perintah pendaftaran device owner:
   ```
   adb shell dpm set-device-owner id.sch.smkwhapo.cbtexambrowser/.CbtDeviceAdminReceiver
   ```
6. Kalau muncul `Success: Device owner set to package...`, berarti berhasil. Sekarang buka aplikasinya
   — mode kiosk akan aktif langsung tanpa dialog screen pinning.

**Catatan:**
- Proses ini harus diulang di **setiap** perangkat ujian secara manual (tidak bisa lewat Play Store/OTA).
- Untuk melepas status device owner (misalnya HP mau dipakai normal lagi), jalankan:
  ```
  adb shell dpm remove-active-admin id.sch.smkwhapo.cbtexambrowser/.CbtDeviceAdminReceiver
  ```
  atau factory reset perangkat.
- Untuk puluhan/ratusan perangkat sekaligus, cara manual `adb` di atas cukup merepotkan — pertimbangkan
  provisioning lewat **QR code** (Android Enterprise "zero-touch"/"AFW#" provisioning) di lain waktu
  kalau jumlah perangkatnya banyak.

## Yang sebaiknya dikembangkan lanjut

1. **Logo asli** — ganti `app/src/main/res/drawable/ic_launcher.xml` dengan logo sekolah yang
   sesungguhnya (lewat Image Asset Studio di Android Studio untuk hasil adaptive-icon yang rapi).
2. **Build release + signing** — versi debug cukup untuk uji coba; untuk dibagikan ke banyak HP
   siswa, build varian **release** yang ditandatangani (lebih stabil, tanpa watermark debug).
3. **Perkuat kontrol keluar** — saat ini tombol keluar bisa dipakai siapa saja (sesuai permintaan).
   Kalau nanti perlu mencegah siswa keluar sendiri saat ujian berlangsung, tombol ini bisa ditambah
   PIN admin.
4. **Deteksi jaringan terputus** — tambah pengecekan koneksi otomatis & retry berkala saat sinyal
   putus di tengah ujian.
5. **Blokir notifikasi masuk** selama sesi ujian — sudah memungkinkan sekarang karena aplikasi bisa
   jadi device owner; tinggal tambah `dpm.setStatusBarDisabled(admin, true)` di `MainActivity`.
