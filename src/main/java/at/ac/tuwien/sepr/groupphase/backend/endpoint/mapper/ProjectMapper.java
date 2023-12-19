package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailLazyProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ProjectMapper {

    
    public DetailProjectDto projectToDetailProjectDto(Project project);

    public List<DetailProjectDto> projectToDetailProjectDto(List<Project> projects);

    public List<DetailLazyProjectDto> projectToDetailLazyProjectDto(List<Project> projects);
}
