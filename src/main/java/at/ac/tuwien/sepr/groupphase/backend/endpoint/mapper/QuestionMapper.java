package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@Mapper
public interface QuestionMapper {
    @Named("questionToQuestionDto")
    QuestionDto questionToQuestionDto(Question question);

    @IterableMapping(qualifiedByName = "questionToQuestionDto")
    List<QuestionDto> questionToQuestionDto(List<Question> questionnaire);


    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "category", ignore = true)
    Question questionDtoToQuestion(QuestionDto questionDto);
}
