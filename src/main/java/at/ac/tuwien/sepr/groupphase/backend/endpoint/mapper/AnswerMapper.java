package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AnswerDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface AnswerMapper {
    @Named("answerToAnswerDto")
    @Mapping(target = "emailUser", source = "answer.applicationUser.email")
    AnswerDto answerToAnswerDto(Answer answer);

    @IterableMapping(qualifiedByName = "answerToAnswerDto")
    List<AnswerDto> answerToAnswerDto(List<Answer> answers);

    @Named("answerDtoToAnswer")
    @Mapping(target = "applicationUser", ignore = true)
    @Mapping(target = "id", ignore = true)
    Answer answerDtoToAnswer(AnswerDto answer);

    //user email cannot be mapped to applicationUser!
    @IterableMapping(qualifiedByName = "answerDtoToAnswer")
    @Mapping(target = "question", source = "answer.question")
    List<Answer> answerDtoToAnswer(List<AnswerDto> answers);

    QuestionDto questionToQuestionDto(Question question);

    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "category", ignore = true)
    Question questionDtoToQuestion(QuestionDto question);

}
//@Mapping(target = "question", source = "answer.question")
