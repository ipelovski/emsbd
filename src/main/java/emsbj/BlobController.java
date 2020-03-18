package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/blobs")
public class BlobController {
    public static final String uploadProfilePicture = "uploadProfilePicture";
    @Autowired
    private BlobRepository blobRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Extensions extensions;

    @GetMapping(WebMvcConfig.objectIdPathParam)
    @ResponseBody
    public ResponseEntity<byte[]> details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long blobId
    ) {
        Optional<Blob> optionalBlob = blobRepository.findById(blobId);
        if (optionalBlob.isPresent()) {
            Blob blob = optionalBlob.get();
            return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(blob.getMimeType()))
                .body(blob.getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/profile-picture" + WebMvcConfig.objectIdPathParam,
        name = uploadProfilePicture)
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
        @PathVariable(WebMvcConfig.objectIdParamName) Long userId,
        @RequestParam("file") MultipartFile file
    ) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Blob blob = new Blob();
            blob.setMimeType(file.getContentType());
            try {
                blob.setData(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blobRepository.save(blob);
            if (user.getPersonalInfo().getPicture() != null) {
                Long oldBlobId = user.getPersonalInfo().getPicture().getId();
                user.getPersonalInfo().setPicture(null);
                userRepository.save(user);
                blobRepository.deleteById(oldBlobId);
            }
            user.getPersonalInfo().setPicture(blob);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>(2);
            response.put("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
            response.put("path", extensions.getURLs().blob(blob));
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(response);
        } else {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Collections.singletonMap("error", "No user found!"));
        }
    }
}
