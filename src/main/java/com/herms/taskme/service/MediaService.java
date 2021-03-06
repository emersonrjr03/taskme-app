package com.herms.taskme.service;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.herms.taskme.dto.MediaForCreationDTO;
import com.herms.taskme.model.CloudinaryManager;
import com.herms.taskme.model.Media;
import com.herms.taskme.repository.MediaRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class MediaService {
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private CloudinaryManager cloudinaryManager;

    public MediaService() {
    }

    public List<Media> getAllMedia(){
        List<Media> mediaList = new ArrayList<>();
        mediaRepository.findAll().forEach(mediaList::add);
        return mediaList;
    }

    public Media getMedia(Long id){
        return mediaRepository.findById(id).get();
    }

    public void addMedia(Media media){
        media.setCreatedOn(new Date());
        mediaRepository.save(media);
    }

    public Media addMedia(MediaForCreationDTO mediaForCreationDTO) throws IOException {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(mediaForCreationDTOToMedia);

        if(mediaForCreationDTO.getFileByteArray().length > 0){
            Map uploadResult = cloudinaryManager.getCloudinaryInstance().uploader().upload(mediaForCreationDTO.getFileByteArray(), ObjectUtils.emptyMap());

            mediaForCreationDTO.setUrl((String) uploadResult.get("url"));
            mediaForCreationDTO.setPublicId((String) uploadResult.get("public_id").toString());

            //map imageForCreationDTO to a Media, delete the current profile photo and persist this new one
            Media media = new Media();
            modelMapper.map(mediaForCreationDTO, media);
            return mediaRepository.save(media);
        }
        return null;
    }

    public void updateMedia(Long id, Media media) {
        mediaRepository.save(media);
    }

    public void deleteMedia(Long id) throws IOException {
        Media toBeDeleted = getMedia(id);
        deleteMedia(toBeDeleted);
    }

    public void deleteMedia(Media media) throws IOException {
        cloudinaryManager.getCloudinaryInstance().uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());
        mediaRepository.delete(media.getId());
    }

    public Media updateMediaContentOnCloud(Media media, byte[] content) throws IOException {
        if(content.length > 0){
            if(media.getPublicId() != null){
                Map deleteResult = cloudinaryManager.destroy(media.getPublicId(), ObjectUtils.emptyMap());
            }
            Map uploadResult = cloudinaryManager.getCloudinaryInstance().uploader().upload(content, ObjectUtils.emptyMap());

            media.setUrl((String) uploadResult.get("url"));
            media.setPublicId((String) uploadResult.get("public_id").toString());

            return mediaRepository.save(media);
        }
        return media;
    }


    public void deleteMediaIdListAsync(List<Long> mediaIdList) throws InterruptedException, IOException {
        for(Long mediaId : mediaIdList){
            deleteMedia(mediaId);
        }
        System.out.println("Media list successfully deleted");
        //return CompletableFuture.completedFuture("Media list successfully deleted");
    }

    PropertyMap<MediaForCreationDTO, Media> mediaForCreationDTOToMedia = new PropertyMap<MediaForCreationDTO, Media>() {
        protected void configure() {
            skip().setId(null);
        }
    };

}
