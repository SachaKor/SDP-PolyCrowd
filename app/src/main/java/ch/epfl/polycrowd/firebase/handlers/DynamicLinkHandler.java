package ch.epfl.polycrowd.firebase.handlers;

import android.net.Uri;

import java.text.ParseException;


public interface DynamicLinkHandler {
    void handle(Uri deepLink);
}
