package living.word.livingword.service;

import living.word.livingword.entity.GiftsAssessment;
import living.word.livingword.entity.User;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.GiftsAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GiftsAssessmentService {

    @Autowired
    private GiftsAssessmentRepository giftsAssessmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    // Save or update user's gifts assessment
    public void saveGiftsAssessment(Long userId, List<String> answers) {
        Optional<User> user = appUserRepository.findById(userId);

        if (user.isPresent()) {
            GiftsAssessment assessment = giftsAssessmentRepository.findByUserId(userId)
                    .orElse(new GiftsAssessment());

            assessment.setUser(user.get());
            assessment.setAnswers(answers);  // Ensure GiftsAssessment has an answers field in your entity

            giftsAssessmentRepository.save(assessment);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    // Get the gifts assessment for a user
    public GiftsAssessment getGiftsAssessment(Long userId) {
        return giftsAssessmentRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
    }
}
