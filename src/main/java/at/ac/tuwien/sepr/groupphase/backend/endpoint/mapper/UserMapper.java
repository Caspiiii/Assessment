package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserMapper {

    @Mapping(target = "username", source = "email")
    @Mapping(target = "mode", source = "role")
    UserRegisterDto applicationUserToRegisterUserDto(ApplicationUser applicationUser);

    List<DetailUserDto> applicationUserToDetailUserDto(List<ApplicationUser> applicationUsers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    ApplicationUser simpleUserDtoToApplicationUser(SimpleUserDto detailUserDto);

    @Mapping(target = "departmentName", source = "department.name")
    SimpleUserDto applicationUserToDetailedUserDto(ApplicationUser applicationUser);

}
