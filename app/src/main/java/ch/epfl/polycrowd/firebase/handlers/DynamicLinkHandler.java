package ch.epfl.polycrowd.firebase.handlers;

import android.net.Uri;


public interface DynamicLinkHandler {
    void handle(Uri deepLink);
}
