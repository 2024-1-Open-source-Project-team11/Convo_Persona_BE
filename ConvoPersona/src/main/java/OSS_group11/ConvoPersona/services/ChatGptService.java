package OSS_group11.ConvoPersona.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ChatGptService {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ModerationService moderationService;


    @Autowired
    public ChatGptService(WebClient.Builder webClientBuilder, ModerationService moderationService) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .build();
        this.moderationService = moderationService;
    }

    public String callChatGPTAPI(String mbti, String userPrompt, String historyChat) throws JsonProcessingException {
        // 사용자 메시지와 시스템 프롬프트를 포함한 요청 본문 생성
        String mbtiText =
                """
                        에너지 방향 (E-I)
                                                
                        외향 (E, Extraversion)
                        에너지와 주의를 주로 외부 세계, 다른 사람들, 활동에 집중하는 성향이다. 외향형 인물은 일반적으로 사회적이며, 다른 사람과의 상호 작용에서 에너지를 얻는다.
                        내향 (I, Introversion)
                        에너지와 주의를 주로 내면의 생각, 감정, 상상에 집중하는 성향이다. 내향형 인물은 일반적으로 독립적이며, 혼자 있는 시간에서 에너지를 얻는다.
                                                
                        정보 수집 방식 (S-N)
                                                
                        감각 (S, Sensing)
                        현재의 실제적이고 구체적인 사실에 기반하여 정보를 수집하는 성향이다. 세부 사항에 주의를 기울이며 경험에 의존한다.
                        직관 (N, Intuition)
                        가능성, 미래, 추상적 개념에 초점을 맞추며 정보를 수집하는 성향이다. 전체적인 그림을 보며 새로운 가능성을 탐구한다.
                                                
                        의사 결정 방식 (T-F)
                                                
                        사고 (T, Thinking)
                        논리와 원칙을 기반으로 의사결정을 하는 성향이다. 객관적이며 분석적으로 문제를 접근한다.
                        감정 (F, Feeling)
                        가치, 사람의 감정, 상황에 따른 조화를 중시하여 의사결정을 하는 성향이다. 사람 중심적이며 동정심이 있다.
                                                
                        생활 방식 (J-P)
                                                
                        판단 (J, Judging)
                        계획적이고 체계적으로 일을 추진하는 성향이다. 일을 끝내는 것을 선호하며, 명확한 계획과 구조를 선호한다.
                                                
                        인식 (P, Perceiving)
                        유연하고 개방적인 방식으로 일을 추진하는 성향이다. 새로운 정보와 선택을 열어두는 것을 선호하며, 자유로운 흐름을 선호한다
                                                
                        ISTJ (내향-감각-사고-판단)
                        ISTJ는 신뢰할 수 있고 책임감이 강한 유형이다. 이들은 자신의 의무와 책임을 매우 중요하게 생각하며, 일을 철저히 완료하기 위해 노력한다. 이들은 현실적이고 논리적으로 생각하며, 구체적인 사실과 정보에 기반하여 결정을 내린다.
                                                
                        강점
                        신뢰성: ISTJ는 약속을 지키려는 경향이 있으며, 이들이 맡은 업무를 성실하게 완료한다.
                        상세함: 이들은 세부사항에 주의를 기울이며, 정보와 사실을 철저하게 분석한다.
                        체계적 접근: 일정, 계획 및 절차를 따르는 데 탁월하며, 효율적으로 일을 진행하기 위해 체계를 선호한다.
                                                
                        도전과제
                        변화에 대한 저항: ISTJ는 전통과 익숙한 방식을 선호하기 때문에, 새로운 방식이나 변화에 대해 망설일 수 있다.
                        감정 표현의 어려움: 이들은 내향적인 성향 때문에 감정을 표현하는 데 어려움을 느낄 수 있으며, 이로 인해 다른 사람들과의 관계에서 오해가 발생할 수 있다.
                                                
                        대인 관계
                        친밀한 관계에서는 따뜻하고 신뢰할 수 있지만, 이들은 일반적으로 자신의 생각과 감정을 공개하는 것을 피한다. 직설적이고 사실적인 대화를 선호하며, 불필요한 소통이나 감정적인 대화를 피하는 경향이 있다.
                                                
                        ISFJ (내향-감각-감정-판단)
                        ISFJ는 진실되고 헌신적인 사람들로 알려져 있다. 이들은 다른 사람들을 돌보는 것을 중요하게 생각하며, 안정감과 조화를 추구한다. 또한, 사실과 세부 정보에 주의를 기울이며, 현실적인 상황과 경험을 기반으로 판단한다.
                                                
                        강점
                        관찰력: 이들은 주변 환경과 사람들에 대한 민감한 관찰력을 갖추고 있다.
                        책임감: ISFJ는 맡은 일에 대한 강한 책임감을 가지며, 다른 사람들을 위해 노력한다.
                        인내심: 이들은 인내심이 강하며, 어려운 상황에서도 차분하게 대응할 수 있다.
                                                
                        도전과제
                        과도한 헌신: 너무나도 다른 사람들을 위해 헌신하는 경향이 있어, 자신의 필요와 감정을 무시하거나 소홀히 할 수 있다.
                        변화에 대한 저항: 이들은 변화를 좋아하지 않으며, 새로운 상황이나 변화를 받아들이는 데 시간이 필요할 수 있다.
                                                
                        대인 관계
                        사람들과의 깊은 관계를 중요하게 생각하며, 주변 사람들에게 따뜻한 관심과 배려를 보인다. 이들은 타인의 감정과 기분을 잘 이해하며, 다른 사람의 입장에서 생각하고 행동하는 경향이 있다.
                                                
                        INFJ (내향-직관-감정-판단)
                        INFJ는 복잡한 내적 생각과 감정의 세계를 가지고 있다. 이들은 사회의 의미와 목적에 대한 깊은 이해를 추구하며, 자신의 가치와 신념에 따라 삶을 살아간다. 미래를 내다보며 가능성을 탐색하고, 깊은 통찰력과 직관을 통해 상황이나 사람들에 대한 통찰력을 발전시킨다.
                                                
                        강점
                        통찰력: INFJ는 본질적인 사실과 상황을 깊이 이해하는 능력을 가지고 있다. 이들은 사람들과 상황을 빠르게 이해할 수 있다.
                        열정: 이들은 자신이 중요하게 생각하는 원인이나 목표를 위해 큰 열정을 지니고 있다.
                        배려심: 다른 사람의 감정과 기분에 민감하며, 사람들을 이해하고 돕는 데 탁월하다.
                                                
                        도전과제
                        과도한 완벽주의: 이들은 종종 완벽을 추구하며, 이로 인해 스스로에게 너무 많은 압박을 가하게 될 수 있다.
                        타인의 감정에 너무 민감: INFJ는 다른 사람의 감정과 기분에 너무 민감하게 반응할 수 있어, 스스로를 지켜내기 어려울 때가 있다.
                                                
                        대인 관계
                        깊고 의미 있는 관계를 선호한다. 이들은 종종 다른 사람의 감정과 생각을 깊이 이해하고, 타인을 위해 깊은 동정심과 이해를 표현한다. 내적인 생각과 감정을 표현하는 데 조심스럽지만, 친밀하게 지내는 사람들과는 깊은 연결을 느낄 수 있다.
                                                
                        INTJ (내향-직관-사고-판단)
                        INTJ는 독립적이며, 깊은 통찰력과 전략적 사고 능력을 갖추고 있다. 이들은 자신의 아이디어와 미래에 대한 비전을 확고히 가지며, 이를 실현하기 위한 계획을 세우는 데 능숙하다. 복잡한 문제를 해결하는 것을 좋아하며, 자신만의 독특한 방법으로 접근한다.
                                                
                        강점
                        전략적 사고: 이들은 미래 지향적이며, 장기적인 목표와 계획을 세우는 데 능력이 있다
                        결정력: 일단 목표를 정하면 그것을 향해 직설적이고 체계적으로 나아간다.
                        독립적인 성향: 이들은 자신의 생각과 아이디어에 확신을 가지고 있으며, 타인의 의견에 크게 휘둘리지 않는다.
                                                
                        도전과제
                        대인 관계의 어려움: INTJ는 감정의 표현이나 소통에서 어려움을 느낄 수 있으며, 이로 인해 다른 사람들과의 관계에서 오해가 발생할 수 있다.
                        과도한 비판성: 이들은 자신과 타인에게 매우 높은 기준을 가지며, 때로는 너무 비판적으로 다가올 수 있다.
                                                
                        대인 관계
                        종종 감정보다는 논리와 이성에 기반하여 의사소통을 한다. 그러나 이들은 깊은 관계와 진지한 대화를 선호하며, 자신과 비슷한 가치관을 가진 사람들과 잘 어울린다. 자신의 생각과 감정을 쉽게 공개하지 않지만, 신뢰하는 사람들과는 깊은 연결을 나누려고 노력한다.
                                                
                        ISTP (내향-감각-사고-인식)
                        ISTP는 현실적이며, 분석적으로 사물을 접근한다. 이들은 실제적인 문제 해결에 능숙하며, 상황에 따라 유연하게 대응한다. 손으로 무언가를 만드는 것이나 기계를 다루는 것을 좋아하는 경우가 많다.
                                                
                        강점
                        문제 해결 능력: ISTP는 즉석에서 빠르고 효율적으로 문제를 해결하는 데 능숙하다. 이들은 사물이나 상황을 분해하고, 핵심을 파악하는 데 능하다.
                        유연성: 이들은 계획에 얽매이지 않고, 상황에 따라 적절하게 대응하는 능력이 있다.
                        실용성: ISTP는 현실적이며 구체적인 정보와 직접적인 경험을 중시한다.
                                                
                        도전과제
                        감정 표현의 부재: 이들은 종종 자신의 감정을 표현하는 데 어려움을 느낄 수 있다.
                        장기 계획의 부재: ISTP는 순간순간의 상황에 집중하기 때문에 장기적인 계획이나 전략을 세우는 데 어려움을 느낄 수 있다.
                                                
                        대인 관계
                        사회적 상황에서는 대체로 조용하고 무표정 할 수 있지만, 자신이 관심 있는 주제에 대해서는 활발하게 의견을 나눈다. 이들은 논리와 사실에 기반하여 의사소통을 하며, 감정적인 대화보다는 구체적이고 실제적인 정보를 주고받는 것을 선호한다
                                                
                        ISFP (내향-감각-감정-인식)
                        ISFP는 삶의 순간들을 소중하게 여기며, 현실적이면서도 주변 환경과 조화를 이루려고 노력한다. 이들은 유연하고 개방적이며, 상황에 따라 적절하게 대응한다. 깊은 감정을 가지고 있으며, 예술적인 감각이 뛰어나 종종 창작활동에 몰두한다.
                                                
                        강점
                        예술적 감각: 많은 ISFP들이 미술, 음악, 요리, 공예 등 다양한 창작활동에 능숙하다.
                        뛰어난 관찰력: 이들은 주변 환경과 사람들을 잘 관찰하며, 작은 변화나 디테일에도 민감하게 반응한다.
                        따뜻한 마음씨: ISFP는 자신의 감정과 타인의 감정에 민감하며, 주변 사람들을 돌보는 데 있어 진실성을 보인다.
                                                
                        도전과제
                        결정 어려움: 때때로 ISFP는 선택이나 결정을 내리는 데 있어서 고민이 깊어질 수 있다.
                        감정의 충돌: 강한 감정을 겪을 때, 그 감정을 적절히 표현하거나 관리하는 데 어려움을 느낄 수 있다.
                                                
                        대인 관계
                        대체로 사교적이지 않지만, 이들과 가까운 사람들에게는 따뜻하고 친절하다. 이들은 감정에 기반한 진실한 대화와 깊은 연결을 선호하며, 타인의 감정과 생각을 존중하다.
                                                
                        INFP (내향-직관-감정-인식)
                        INFP는 이상주의적이며, 깊은 가치관과 성실성을 가지고 있다. 이들은 자신의 내면의 세계와 감정을 중시하며, 자신만의 독특한 방식으로 세상을 바라본다. 자신의 가치와 신념을 바탕으로 결정을 내리며, 개인적인 정체성에 큰 중요성을 둔다.
                                                
                        강점
                        이상주의: INFP는 고결한 이상과 꿈을 추구하며, 그것을 실현시키기 위해 노력한다.
                        따뜻한 마음씨: 이들은 타인에게 이해심과 공감심을 갖고 접근하며, 사람들을 돌보는 것을 중요하게 생각한다.
                        창의성: 이들은 독특한 관점과 풍부한 상상력을 가지고 있어, 창작 활동이나 예술 분야에서 뛰어난 능력을 발휘할 수 있다.
                                                
                        도전과제
                        과도한 자기 반성: 때로는 자신의 감정과 생각에 너무 몰두하여, 현실 세계와의 연결을 잃을 수 있다.
                        비판에 취약: INFP는 자신의 가치와 신념을 깊이 있게 고민하므로, 타인의 비판이나 부정적인 피드백에 민감하게 반응할 수 있다.
                                                
                        대인 관계
                        초기에는 조용하고 내성적일 수 있지만, 친밀한 관계에서는 자신의 깊은 감정과 생각을 나누는 것을 좋아한다. 이들은 진심어린 대화와 깊은 연결을 선호하며, 타인의 감정과 생각을 존중하고 이해하려 노력한다.
                                                
                        INTP (내향-직관-사고-인식)
                        INTP는 본질적으로 분석가이며, 이들은 끊임없이 새로운 아이디어와 이론을 탐색한다. 이들은 복잡한 문제를 해결하는 데 재능이 있으며, 주변 환경에서 관찰한 패턴과 연결점을 발견하는 데 능숙하다. 자유롭고 독립적인 사고를 중시하며, 자신만의 방식으로 세상을 이해하려고 노력한다.
                                                
                        강점
                        논리적 사고: 이들은 논리와 분석을 통해 문제를 해결하고, 일관된 근거와 이유를 중요하게 생각한다.
                        독립적인 성격: INTP는 자신의 생각과 판단을 믿습니다. 이들은 타인의 기대나 틀에 얽매이지 않고 자신의 길을 찾아간다.
                        창의력: 이들은 새로운 관점과 아이디어를 지속적으로 탐구하며, 기존의 방식을 도전하는 것을 두려워하지 않는다.
                                                
                        도전과제
                        감정 표현: 이들은 감정적인 상황이나 대화에서 자신을 표현하는 데 어려움을 느낄 수 있습니다. 때로는 자신의 감정을 무시하거나 회피하는 경향이 있다.
                        완성의 어려움: 많은 아이디어와 계획을 가지고 있지만, 시작한 일을 끝마치는 것이 어려울 수 있다.
                                                
                        대인 관계
                        초기의 상황에서는 내성적이고 거리감을 둘 수 있지만, 친한 사람들에게는 이들만의 유머 감각이나 통찰을 나눈다. 이들은 표면적인 대화보다는 깊이 있는 토론을 선호하며, 같은 관심사를 가진 사람과는 잘 어울린다.
                                                
                        ESTP (외향-감각-사고-인식)
                        ESTP는 현재 순간을 즐기는 사람으로, 실제 경험을 중시한다. 이들은 기회를 즉시 잡아 취하고, 어떤 상황에서든 적응력이 뛰어나다. 사실을 기반으로 빠르게 정보를 처리하고, 직관적으로 문제를 해결하는 데 재능이 있다.
                                                
                        강점
                        적응성: ESTP는 변화하는 환경이나 새로운 상황에서도 빠르게 적응하며 행동한다.
                        현실감: 이들은 현실에 기반한 명확하고 구체적인 정보를 선호하며, 이를 통해 즉각적인 결정을 내릴 수 있다.
                        에너지: 이들은 에너지가 넘치고 활동적이며, 이들과 함께하는 사람들에게도 에너지를 전달한다.
                                                
                        도전과제
                        장기 계획: ESTP는 현재의 순간에 중점을 두기 때문에 장기적인 계획이나 목표 설정에 어려움을 느낄 수 있다.
                        인내심 부족: 이들은 빠른 결과를 원하기 때문에, 지체되거나 오랜 시간이 소요되는 일에 대한 인내심이 부족할 수 있다.
                                                
                        대인 관계
                        사교적이며, 다양한 사람들과의 교류를 즐긴다. 이들은 주변 사람들을 잘 챙기며, 분위기 메이커 역할을 하는 경우가 많다. 직설적이며, 자신의 의견을 솔직하게 표현하는 경향이 있다.
                                                
                        ESFP (외향-감각-감정-인식)
                        ESFP는 현재 순간을 즐기며, 주변 환경과 사람들에게 열린 사람들이다. 이들은 활기차고 사교적이며, 주변에서 일어나는 일에 즉각적으로 반응한다. 감정적인 경험을 중시하며, 사람들과의 관계에서 따뜻함과 친근함을 중요하게 생각한다.
                                                
                        강점
                        사교성: ESFP는 다양한 사람들과의 교류를 즐기며, 쉽게 친구를 사귈 수 있다.
                        실제적인 관점: 이들은 현실감 있게 상황을 바라보고, 실제적인 해결책을 제시한다.
                        즉흥적인 행동: 이들은 순간의 감정과 상황에 따라 즉각적으로 행동하며, 빠르게 변화하는 환경에 잘 적응한다.
                                                
                        도전과제
                        장기 계획의 부족: 현재 순간에 집중하는 경향이 있어, 장기적인 계획이나 목표 설정에 어려움을 느낄 수 있다.
                        과도한 감정 표현: 때로는 감정에 휩싸여 과도한 반응을 보일 수 있으며, 이로 인해 주변 사람들과의 갈등이 발생할 수 있다.
                                                
                        대인 관계
                        주변 사람들을 기쁘게 해주는 것을 좋아하며, 다른 사람의 감정에 민감하게 반응한다. 이들은 경청하는 데 재능이 있으며, 다른 사람의 문제나 감정에 따뜻하게 공감한다.
                                                
                        ENFP (외향-직관-감정-인식)
                        ENFP는 열정적이며 창의적인 사람들로, 가능성을 탐구하는 데 큰 흥미를 갖는다. 이들은 새로운 아이디어와 기회에 대해 열린 마음을 가지고 있다. 사람들과의 관계를 깊게 가치하며, 다양한 사람들과의 교류를 통해 자신의 세계를 확장하는 것을 즐긴다.
                                                
                        강점
                        창의성: ENFP는 새로운 아이디어나 접근 방식에 대해 항상 생각한다. 이들은 다양한 관점을 고려하며 문제를 해결하는 데 재능이 있다.
                        열정: 이들은 자신이 관심 있는 분야나 활동에 대해 깊은 열정을 보이며, 주변 사람들에게도 그 열정을 전달한다.
                        사교성: ENFP는 다른 사람들과의 교류를 즐기며, 넓은 인맥을 유지하는 데 재능이 있다.
                                                
                        도전과제
                        일의 완성: 이들은 새로운 아이디어나 프로젝트에 쉽게 흥미를 느끼지만, 시작한 일을 완성하는 데 어려움을 겪을 수 있다.
                        과민성: ENFP는 감정적인 사람들로, 때로는 자신이나 다른 사람의 감정에 과도하게 반응할 수 있다.
                                                
                        대인 관계
                        따뜻하고 친근한 성격으로, 다른 사람의 감정과 생각에 민감하게 반응한다. 이들은 다른 사람의 기분을 빠르게 파악하며, 상대방을 편안하게 만들어줄 수 있다. 이들은 상대방의 감정과 의견을 존중하며, 다양한 사람들과 깊은 관계를 형성하는 데 재능이 있다.
                                                
                        ENTP (외향-직관-사고-인식)
                        ENTP는 창의적이고 지적 호기심이 많은 사람들이다. 이들은 새로운 아이디어나 접근법을 고안하는 것을 좋아하며, 다양한 관점과 가능성을 탐구한다. 유연하며 적응력이 뛰어나, 변화와 도전을 두려워하지 않는다.
                                                
                        강점
                        창의적 사고: 이들은 기존의 방식이나 전통에 얽매이지 않고, 독창적이고 창의적인 아이디어를 발휘한다.
                        빠른 지적 반응: ENTP는 토론이나 논의에서 상대방의 말을 빠르게 파악하고, 논리적으로 반응한다.
                        열린 마음: 이들은 새로운 아이디어나 제안을 받아들이는 데 개방적이다.
                                                
                                                
                        도전과제
                        집중 부족: 다양한 관심사를 가진 ENTP는 한 가지 일에 오래 집중하기보다는 여러 일을 동시에 진행하려는 경향이 있다.
                        과도한 논쟁: 이들은 지적인 논쟁을 즐기지만, 때로는 필요 이상으로 토론을 이어나가, 상대방의 감정을 상하게 할 수 있다.
                                                
                        대인 관계
                        다른 사람들의 관점이나 아이디어를 존중하며, 이들과의 교류를 통해 새로운 지식이나 정보를 얻는 것을 즐긴다. 그러나 때로는 너무 논리적이거나 비판적으로 보일 수 있어, 상대방의 감정에 민감하게 반응하는 것이 중요하다.
                                                
                                                
                        ESTJ (외향-감각-사고-판단)
                        ESTJ는 실질적이고 조직적인 성격을 가진 사람들이다. 이들은 논리와 체계를 중시하며, 일관성 있고 실용적인 접근법을 선호한다. 전통과 규칙을 중요하게 생각하며, 주어진 책임을 성실하게 이행하는 경향이 있다.
                                                
                        강점
                        체계적인 조직 능력: ESTJ는 정보를 체계적으로 정리하고, 목표를 달성하기 위한 구체적인 계획을 세우는 데 능숙하다.
                        결단력: 이들은 빠른 결정을 내리는 데 재능이 있으며, 명확한 판단을 기반으로 효과적인 행동을 취한다.
                        책임감: 일이나 임무에 대한 강한 책임감을 가지고 있어, 시작한 일은 반드시 끝까지 완수하려고 한다.
                                                
                        도전과제
                        유연성 부족: 고정된 방식이나 절차에 너무 의존하기 때문에, 새로운 상황이나 변화에 대응하는 데 어려움을 겪을 수 있다.
                        감정적 반응: ESTJ는 감정보다 논리와 사실을 중시하므로, 타인의 감정에 민감하게 반응하지 못하는 경우가 있다.
                                                
                        대인 관계
                        일상의 관계나 직장에서의 상호작용에서 명확한 기대치와 경계를 설정한다. 이들은 다른 사람들에게도 동일한 기준과 기대치를 가진다. 이들은 팀의 구성원으로서 책임감을 갖고, 효율적인 결과를 추구한다. 하지만 때로는 너무 지시적이거나 지배적으로 행동할 수 있다.
                                                
                        ESFJ (외향-감각-감정-판단)
                        ESFJ는 협조적이고 따뜻한 사람들로, 타인의 느낌과 필요에 민감하게 반응한다. 이들은 주변 사람들과의 관계와 조화를 중요하게 생각한다. 실제적이며, 현재의 상황과 사실에 기초해 결정을 내리려고 한다.
                                                
                        강점
                        타인을 배려: 이들은 주변 사람들의 감정과 필요를 잘 이해하고, 지지와 도움을 주려고 노력한다.
                        조직력: ESFJ는 이벤트나 모임을 조직하고 관리하는 데 탁월한 능력을 지니고 있다.
                        책임감: 이들은 주어진 임무나 책임에 대해 신중하게 대응하며, 약속을 지키려고 한다.
                                                
                        도전과제
                        과도한 관심: 때로는 타인의 문제나 감정에 너무 심취하여 자신의 감정이나 필요를 무시할 수 있다.
                        변화에 대한 저항: 이들은 안정감과 일상을 중요하게 생각하기 때문에, 갑작스러운 변화나 불확실성을 좋아하지 않는다.
                                                
                        대인 관계
                        사회적 활동을 즐기며, 다양한 사람들과의 교류에서 편안함을 느낀다. 이들은 직접적인 피드백과 인정을 통해 누군가에게 자신의 관심과 지지를 표현하는 경향이 있다.
                                                
                        ENFJ (외향-직관-감정-판단)
                        ENFJ는 따뜻하고, 통찰력 있으며, 타인의 감정과 필요에 깊게 공감한다. 이들은 타인을 도와주며 긍정적인 변화를 이끌어내는 데에 재능이 있다. 미래 지향적인 생각을 가지며 가능성과 잠재력에 집중한다.
                                                
                        강점
                        인간 중심: 이들은 타인의 감정과 의견을 잘 이해하며, 대인 관계에서의 조화와 협력을 중시한다.
                        통찰력: ENFJ는 직관을 통해 상황을 통찰하고, 미래의 가능성을 예측하는 데에 재능이 있다.
                        동기 부여: 이들은 자신과 주변 사람들에게 긍정적인 에너지를 주며, 목표 달성을 위해 팀을 동기 부여한다.
                                                
                        도전과제
                        과도한 기대: 때로는 주변 사람들에게 높은 기대치를 가질 수 있어, 실망하는 경우가 있다.
                        자신의 필요 무시: 타인을 너무 신경 쓰다보니 자신의 감정이나 필요를 무시하거나 잊어버릴 수 있다.
                                                
                        대인 관계
                        주변 사람들과 깊은 관계를 형성하려고 노력하며, 대화와 소통을 통해 타인과의 연결을 강화한다. 이들은 사람들이 자신의 잠재력을 최대한 발휘할 수 있도록 도와주는 것을 중요하게 생각한다.
                                                
                        ENTJ (외향-직관-사고-판단)
                        ENTJ는 리더십이 뛰어나고, 계획적이며, 효율성을 추구하는 사람들이다. 이들은 큰 그림을 볼 수 있으며, 복잡한 문제나 상황을 구조적으로 접근하여 해결하는 능력이 있다. 미래 지향적이며, 전략적으로 사고하고 계획을 세우는 것을 좋아한다
                                                
                        강점
                        결단력: 이들은 빠르고 명확하게 결정을 내리며, 목표 달성을 위해 필요한 행동을 취한다.
                        조직력: 복잡한 상황이나 문제에 대해 체계적이고 구조적으로 접근하는 능력이 있다.
                        전략적 사고: 이들은 장기적인 목표와 비전을 세우고 이를 달성하기 위한 전략을 계획한다.
                                                
                        도전과제
                        간섭: 때로는 자신의 의견이나 방식이 옳다고 생각하여, 다른 사람의 의견이나 방식을 과도하게 지배하려는 경향이 있을 수 있다.
                        무감각: 결정을 내릴 때 너무 이성적으로 접근하여, 타인의 감정이나 입장을 간과할 수 있다.
                                                
                        대인 관계
                        명확하고 효율적인 커뮤니케이션을 선호하며, 논리적이고 구조적인 대화를 통해 다른 사람들과 의사소통한다. 이들은 자신의 의견과 생각을 분명하게 표현하며, 타인의 의견에 대해서도 명확한 피드백을 제공한다.
                        """;

        // userPrompt moderaiton으로 검열하기
        Boolean isModerationBlocked = moderationService.moderateText(userPrompt).block();
        String moderationResult = (isModerationBlocked != null) ? isModerationBlocked.toString() : "unknown";
        System.out.println("moderationResult = " + moderationResult);     //moderation 결과 출력
        String moderationText = "유해한 내용이 감지되었습니다. 다른 주제로 대화를 이어나가 주시겠어요?";

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", String.format(
                "사용자의 MBTI는 %s입니다.\n" +
                        "사용자가 친근함을 느끼도록 반말로 상담을 진행해주세요.\n" +
                        "사용자의 MBTI를 고려하여 맞춤 고민 상담을 진행해주세요.\n" +
                        "답변할 때마다, 사용자의 MBTI는 언급하지말고 답변하세요.\n" +
                        "대화 중간에 다른 주제에 대해서 얘기하거나 질문하면 절대 대답하지 않고 현재 주제에 대해서 대화해달라고 권유하며 대화를 진행해주세요.\n" +
                        "한 주제에 대해서 고민상담이 끝났다고 판단되면 안부인사와 함께 대화를 종료해주세요.\n" +
                        "대화가 종료되고 나서 사용자가 대화를 시도하려고 하면 대화가 종료되었다고 알려주고 새로고침을 시도하여 대화를 다시 시작해달라고 해주세요.\n" +
                        "'%s'를 참고하여 고민상담해주세요.\n" +
                        "moderation 검증 결과는 %s입니다. True라면 적절하지 않거나 유해한 내용이므로 '%s'라고 답변해주세요.\n" +
                        "%s", mbti, mbtiText, moderationResult, moderationText, historyChat));


        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "gpt-4o");
        requestBodyMap.put("messages", List.of(systemMessage, userMessage));
        requestBodyMap.put("temperature", 0.5);
        requestBodyMap.put("max_tokens", 512);
        requestBodyMap.put("top_p", 0.5);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        //chatGPT API 요청 보낼 때, requestBody 테스트
        System.out.println("requestBody = " + requestBody);

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


}
