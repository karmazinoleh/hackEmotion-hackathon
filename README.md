# hackEmotion-hackathon
## This website provides an opportunity to organize the collection of datasets and a smart value selection system through the participation of several participants in the work.
 
### ✅ The following features are currently implemented: 
- [x] Upload sets of photos
- [x] Select emotions that correspond to these photos (so far, only among those created directly in the code) 
- [x] Store them in your S3 storage.

### 📝 Planned to be implemented:
- [ ] Ability to export datasets for further AI training.
- [ ] Creation of author's emotions and other parameters.
- [ ] A smart system for selecting final parameters: each participant will be able to evaluate the image and choose the right emotions, in their opinion. The final calculation will summarize and select the most likely emotion (the one most often chosen by other users).
- [ ] Advanced rating system: getting points for rating images, uploading images.

## Get Started
1. Make sure you installed [npm](https://www.npmjs.com/) and docker!
2. Go to the project's core folder and run ``` docker compose up ```.
3. Run Spring Boot project.
4. Run pgAdmin4 with DB named hackemotions (or change name in properties).
5. Look at *src/main/resources/application-example.properties* and enter your s3 keys.
6. Go to */my-app/my-ap* and run ``` run npm dev ```.
7. Enter *http://localhost:5173/* and register new account.
8. Activate account at *http://localhost:1080/*.

   
