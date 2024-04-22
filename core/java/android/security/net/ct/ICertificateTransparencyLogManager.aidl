package android.security.net.ct;

import android.security.net.ct.CertificateTransparencyLogList;

/** @hide */
interface ICertificateTransparencyLogManager {

    CertificateTransparencyLogList getLogList(String version);
}
