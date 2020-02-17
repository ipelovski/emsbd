package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/blobs")
public class BlobController {
    public static final String uploadProfilePicture = "uploadProfilePicture";
    @Autowired
    private BlobRepository blobRepository;
    @Autowired
    private UserRepository userRepository;

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
    public String uploadProfilePicture(
        @PathVariable(WebMvcConfig.objectIdParamName) Long userId,
        @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes
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
            redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

            return "redirect:/";
        } else {
            return "";
        }
    }
}
