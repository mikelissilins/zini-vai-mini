package lv.zinivaimini.game.web;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lv.zinivaimini.game.domain.MediaAsset;
import lv.zinivaimini.game.repository.MediaAssetRepository;
import lv.zinivaimini.game.service.InvalidGameException;
import lv.zinivaimini.game.service.ResourceNotFoundException;
import lv.zinivaimini.game.web.dto.GameDtos.MediaView;

@RestController
@RequestMapping("/api")
public class MediaController {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp");
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    private final MediaAssetRepository mediaAssets;

    public MediaController(MediaAssetRepository mediaAssets) {
        this.mediaAssets = mediaAssets;
    }

    @PostMapping(value = "/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@ownerAccess.isOwner(authentication)")
    ResponseEntity<MediaView> upload(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > MAX_BYTES || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidGameException("Atļauti PNG, JPEG vai WebP attēli līdz 5 MB.");
        }
        MediaAsset saved = mediaAssets.save(new MediaAsset(
                file.getOriginalFilename() == null ? "image" : file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()));
        String url = "/api/public/media/" + saved.getId();
        return ResponseEntity.created(URI.create(url)).body(
                new MediaView(saved.getId(), saved.getFileName(), saved.getContentType(), saved.getByteSize(), url));
    }

    @GetMapping("/public/media/{id}")
    ResponseEntity<byte[]> read(@PathVariable UUID id) {
        MediaAsset media = mediaAssets.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attēls nav atrasts."));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.getContentType()))
                .cacheControl(CacheControl.noCache())
                .body(media.getData());
    }
}
