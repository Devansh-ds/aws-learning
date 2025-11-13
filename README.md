
# Aws-learning

A curated collection of spring-based AWS projects exploring common use cases like file uploads to S3 and optimization of deployment workflows.

---

## Projects

### 1. **s3-image-uploader**

A Spring Boot project demonstrating secure and structured image uploads to AWS S3 with best practices:

* Upload images with random UUID-based keys, including metadata like content type and uploader info.
* Generate secure pre-signed URLs for time-limited access without exposing S3 buckets publicly.
* Enforce file size limits, per-user storage quotas, and daily upload limits.
* Manage per-user file operations: list, download, delete, with quota adjustments.

### 2. **spring-aws-deploy-demo**

A sample project illustrating deployment integration with AWS:

* Demonstrates deploying Spring Boot applications to AWS EC2.

---

## Usage

1. Clone the repository:

   ```bash
   git clone https://github.com/Devansh-ds/aws-learning.git
   ```
2. Navigate to a project folder, e.g.:

   ```bash
   cd aws-learning/s3-image-uploader
   ```
3. Update `application-sample.yml` to `application.yml` with your AWS credentials, S3 bucket name, region, and local DB config.
4. Run the application:

   ```bash
   mvn spring-boot:run
   ```
5. Use available REST endpoints to upload, list, download, and delete files securely.

---

## Tech Stack

* Java with Spring Boot
* AWS SDK v2 for S3 integration
* Spring Data JPA (PostgreSQL)
* JWT for authentication

---


Would you like help generating a **README for the spring-aws-deploy-demo** module when you’re ready to add its details?
