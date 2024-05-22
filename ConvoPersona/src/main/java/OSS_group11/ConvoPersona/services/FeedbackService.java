package OSS_group11.ConvoPersona.services;


import OSS_group11.ConvoPersona.domain.Feedback;
import OSS_group11.ConvoPersona.domain.Member;
import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.dtos.AddFeedbackReqDTO;
import OSS_group11.ConvoPersona.repositories.FeedbackRepository;
import OSS_group11.ConvoPersona.repositories.MemberRepository;
import OSS_group11.ConvoPersona.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;


    public void saveFeedback(Long memberId, AddFeedbackReqDTO addFeedbackReqDTO) {
        Long messageId = addFeedbackReqDTO.getMessageId();
        String feedbackContent = addFeedbackReqDTO.getFeedbackContent();

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("Member not found")
        );
        Message gptMessage = messageRepository.findById(messageId).orElseThrow(
                () -> new IllegalArgumentException("Message not found")
        );


        //feedback 저장하기
        feedbackRepository.save(
                Feedback.builder()
                        .member(member)
                        .content(feedbackContent)
                        .gptMessage(gptMessage)
                        .build()
        );
    }
}
