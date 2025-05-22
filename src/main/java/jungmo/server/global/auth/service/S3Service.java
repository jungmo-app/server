package jungmo.server.global.auth.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    /**
     * S3에 파일 업로드
     *
     * @param file MultipartFile 형태의 업로드 파일
     * @param userId 사용자 ID
     * @return 업로드된 파일의 S3 URL
     * @throws IOException 파일 처리 예외
     */
//    public String uploadFile(MultipartFile file, Long userId) throws IOException {
//        // S3에 저장될 파일 경로 생성
//        String fileName = "profile-images/" + userId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//
//        // 임시 파일 생성
//        Path tempFile = Files.createTempFile("s3-upload-", file.getOriginalFilename());
//        file.transferTo(tempFile.toFile());
//
//        // S3 업로드 요청 생성
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .contentType(file.getContentType())
//                .build();
//
//        // S3에 파일 업로드
//        s3Client.putObject(putObjectRequest, tempFile);
//
//        // 임시 파일 삭제
//        Files.delete(tempFile);
//
//        // 업로드된 파일의 URL 반환
//        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
//    }
    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        return uploadToS3(file, "profile-images/" + userId + "/");
    }


    public List<String> uploadImages(List<MultipartFile> files) throws IOException {

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            imageUrls.add(uploadToS3(file, "chat-images/"));
        }

        return imageUrls;
    }

    private String uploadToS3(MultipartFile file, String keyPrefix) throws IOException {
        String fileName = keyPrefix + UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 임시 파일 생성
        Path tempFile = Files.createTempFile("s3-upload-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        // S3 업로드 요청
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.getContentType())
            .build();

        s3Client.putObject(putObjectRequest, tempFile);

        Files.delete(tempFile);

        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }
}
