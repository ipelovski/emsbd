package emsbj;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

@Entity
public class BlobData extends Blob {
    @Lob
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getEncodedData() {
        try {
            return URLEncoder.encode(Base64.getEncoder().encodeToString(getData()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
