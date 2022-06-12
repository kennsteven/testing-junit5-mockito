package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    OwnerService ownerService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    OwnerController ownerController;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    void processFindFormWildCardString() {
        //given
        Owner owner = new Owner(1l,"Kenneth", "Alvarado");
        List<Owner> ownerList = new ArrayList<>();
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Alvarado%").isEqualToIgnoringCase(captor.getValue());
    }

    @Test
    void processFindFormWildCardStringWithAnnotation() {
        //given
        Owner owner = new Owner(1l,"Kenneth", "Alvarado");
        List<Owner> ownerList = new ArrayList<>();
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Alvarado%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
    }

    @Test
    void processCreationFormWithBindingErrors() {
        //given
        Owner owner = new Owner(1l,"Kenneth", "Alvarado");
        given(bindingResult.hasErrors()).willReturn(true);

        //when
        String returnedView = ownerController.processCreationForm(owner, bindingResult);

        //then
        then(bindingResult).should(atLeastOnce()).hasErrors();
        then(ownerService).should(never()).save(owner);
        assertThat(returnedView).isEqualTo(VIEWS_OWNER_CREATE_OR_UPDATE_FORM);
    }

    @Test
    void processCreationFormSuccess() {
        Owner owner = new Owner(5l,"Kenneth", "Alvarado");
        given(bindingResult.hasErrors()).willReturn(false);
        given(ownerService.save(any(Owner.class))).willReturn(owner);

        //when
        String returnedView = ownerController.processCreationForm(owner,bindingResult);

        //then
        then(bindingResult).should(atLeastOnce()).hasErrors();
        then(ownerService).should(atLeastOnce()).save(any(Owner.class));

        assertThat(returnedView).isEqualTo(REDIRECT_OWNERS_5);
    }
}