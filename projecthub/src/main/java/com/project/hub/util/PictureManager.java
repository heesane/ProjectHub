package com.project.hub.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.project.hub.model.type.PictureType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class PictureManager {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  public String upload(
      Long userId,
      String projectName,
      MultipartFile multipartFile,
      PictureType pictureType
  ) throws IOException {

    // system_architecture/1_1.jpg
    // erd/1_1.jpg
    String newFileName =
        pictureType.getFolderPrefix() +
            "/" + userId + "_" + projectName +
            Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.")[1];

    File uploadFile = convert(multipartFile)
        .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

    // 업로드한 사진의 URL 반환
    return upload(newFileName, uploadFile);

  }

  private String upload(
      String newFileName,
      File uploadFile
  ) {

    String uploadImageUrl = putS3(uploadFile, newFileName);

    // convert()함수로 인해서 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
    removeNewFile(uploadFile);

    return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환

  }

  private String putS3(
      File uploadFile,
      String fileName
  ) {

    amazonS3.putObject(
        new PutObjectRequest(bucket, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead)  // PublicRead 권한으로 업로드 됨
    );
    return amazonS3.getUrl(bucket, fileName).toString();

  }

  private void removeNewFile(
      File targetFile
  ) {

    if (targetFile.delete()) {
      log.info("파일이 삭제되었습니다.");
    } else {
      log.info("파일이 삭제되지 못했습니다.");
    }

  }

  private Optional<File> convert(
      MultipartFile file
  ) throws IOException {

    String fileOriginalName = file.getOriginalFilename();

    log.info(fileOriginalName);

    File convertFile = new File(Objects.requireNonNull(fileOriginalName)); // 업로드한 파일의 이름

    if (convertFile.createNewFile()) {
      try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        fos.write(file.getBytes());
      }
      return Optional.of(convertFile);
    }

    // 새로 만들어진 파일이 아닌 경우,
    return Optional.empty();
  }

  public ResponseEntity<byte[]> download(
      String fileName,
      PictureType pictureType
  ) throws IOException {

    S3Object awsS3Object = amazonS3.getObject(
        new GetObjectRequest(bucket, pictureType.getFolderPrefix() + "/" + fileName));

    S3ObjectInputStream s3is = awsS3Object.getObjectContent();

    byte[] bytes = s3is.readAllBytes();

    String downloadedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        .replace("+", "%20");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.IMAGE_JPEG);
    httpHeaders.setContentLength(bytes.length);
    httpHeaders.setContentDispositionFormData("attachment", downloadedFileName);

    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

  public boolean diff(
      String originalHashValue,
      MultipartFile newFile
  ) {
    try {
      String newHashValue = calculateSHA256Base64(newFile);
      return !originalHashValue.equals(newHashValue);
    } catch (NoSuchAlgorithmException | IOException e) {
      log.info("Fail to calculate hash value");
    }
    return false;
  }

  public String calculateSHA256Base64(
      MultipartFile file
  ) throws NoSuchAlgorithmException, IOException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = digest.digest(file.getBytes());

    // Base64 인코딩으로 해시 값을 문자열로 변환
    return Base64.getEncoder().encodeToString(hashBytes);
  }

}
