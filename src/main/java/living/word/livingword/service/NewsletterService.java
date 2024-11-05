package living.word.livingword.service;

import living.word.livingword.entity.Newsletter;
import living.word.livingword.repository.NewsletterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class NewsletterService {

    @Autowired
    private NewsletterRepository newsletterRepository;
    @Autowired
    private NotificationService notificationService;

    public Page<Newsletter> getAllNewsletters(int page, int size, boolean descending) {
        Sort sort = descending ? Sort.by("publicationDate").descending() : Sort.by("publicationDate").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return newsletterRepository.findAll(pageable);
    }

    public Optional<Newsletter> getNewsletterById(Long id) {
        return newsletterRepository.findById(id);
    }

    public Newsletter createNewsletter(Newsletter newsletter) {
        Newsletter savedNewsletter = newsletterRepository.save(newsletter);

        // Enviar notificación después de guardar el newsletter
        notificationService.sendNewsletterNotification(savedNewsletter);

        return savedNewsletter;
    }

    public Optional<Newsletter> updateNewsletter(Long id, Newsletter newsletter) {
        return newsletterRepository.findById(id).map(existingNewsletter -> {
            existingNewsletter.setTitle(newsletter.getTitle());
            existingNewsletter.setNewsletterUrl(newsletter.getNewsletterUrl());
            return newsletterRepository.save(existingNewsletter);
        });
    }

    public void deleteNewsletter(Long id) {
        newsletterRepository.deleteById(id);
    }
}

/*        // Enviar notificación después de guardar el newsletter
        notificationService.sendNewsletterNotification(savedNewsletter); */