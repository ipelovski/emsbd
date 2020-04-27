package sasj.controller.blob;

import sasj.data.blob.Blob;
import sasj.web.UrlBuilder;
import sasj.config.WebMvcConfig;
import sasj.data.user.User;
import org.springframework.stereotype.Service;

@Service
public class BlobUrls {
    public String blob(Blob blob) {
        return UrlBuilder.get(BlobController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, blob.getId());
    }

    public String profilePicture(User user) {
        if (user.getPersonalInfo().getPicture() != null) {
            return blob(user.getPersonalInfo().getPicture());
        } else {
            return WebMvcConfig.noProfilePicture;
        }
    }

    public String uploadProfilePicture(User user) {
        return UrlBuilder.get(BlobController.class, BlobController.uploadProfilePicture,
            WebMvcConfig.objectIdParamName, user.getId());
    }
}
