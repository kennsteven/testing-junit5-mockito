package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService specialitySDJpaService;

    @Test
    void testDeleteByObject() {
        //given
        Speciality speciality = new Speciality();

        //when
        specialitySDJpaService.delete(speciality);

        //then
        then(specialtyRepository).should().delete(any(Speciality.class));

    }

    @Test
    void findByIdTest() {

        //given
        Speciality speciality = new Speciality();
        given(specialtyRepository.findById(1L)).willReturn(Optional.of(speciality));

        //when
        Speciality foundSpeciality = specialitySDJpaService.findById(1L);

        //Then
        assertThat(foundSpeciality).isNotNull();
        then(specialtyRepository).should().findById(anyLong());
        then(specialtyRepository).should(times(1)).findById(anyLong());

    }

    @Test
    void deleteById() {
        //given -none

        //when
        specialitySDJpaService.deleteById(1l);
        specialitySDJpaService.deleteById(1l);

        //then
        then(specialtyRepository).should(times(2)).deleteById(anyLong());
        verify(specialtyRepository, times(2)).deleteById(1l);
    }


    @Test
    void delete() {
        specialitySDJpaService.deleteById(1l);
        specialitySDJpaService.deleteById(1l);

        verify(specialtyRepository, atLeastOnce()).deleteById(1l);
    }

    @Test
    void deleteAtLeast() {
        //given

        //when
        specialitySDJpaService.deleteById(1l);
        specialitySDJpaService.deleteById(1l);

        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1L);
    }

    @Test
    void deleteAtMost() {
        //given

        //when
        specialitySDJpaService.deleteById(1l);
        specialitySDJpaService.deleteById(1l);

        //then
        then(specialtyRepository).should(atMost(5)).deleteById(1L);
    }


    @Test
    void deleteAtNever() {

        //when
        specialitySDJpaService.deleteById(1l);
        specialitySDJpaService.deleteById(1l);

        //then
        then(specialtyRepository).should(never()).deleteById(5l);
    }

    @Test
    void testDelete() {
        //when
        specialitySDJpaService.delete(new Speciality());

        //then
        then(specialtyRepository).should(atMost(1)).delete(any(Speciality.class));
    }
}