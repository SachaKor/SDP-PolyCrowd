package ch.epfl.polycrowd;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.Nullable;

import com.google.android.gms.common.util.IOUtils;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Test;
import org.slf4j.helpers.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UtilsTest {


    InputStream empty = new InputStream() {
        @Override
        public int read() {
            return -1;  // end of stream
        }
    };
    InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
    @Test
    public void testGetBytesOnEmptyStream() {

        try {
            byte[] bytes = Utils.getBytes(empty);
            assert(bytes.length == 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetBytes() {

        try {
            byte[] bytes = Utils.getBytes(anyInputStream);
            assert(bytes.length > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test(expected = NullPointerException.class)
    public void testGetBytesOnNullStream() throws IOException {
        byte[] bytes = Utils.getBytes(null);
    }
    @Test(expected = NullPointerException.class)
    public void testGetFileFromNullUri(){
        Utils.getFileNameFromUri(null);
    }


}
