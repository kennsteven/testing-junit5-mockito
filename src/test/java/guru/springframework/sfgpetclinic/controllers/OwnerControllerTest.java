package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OwnerControllerTest {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    OwnerService ownerService;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @InjectMocks
    OwnerController ownerController;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).
                willAnswer(invocation -> {
                    List<Owner> ownerList = new ArrayList<>();

                    String name = invocation.getArgument(0);

                    if(name.equals("%Alvarado%")){
                        ownerList.add(new Owner(1l,"Kenneth", "Alvarado"));
                        return  ownerList;
                    }else if(name.equals("%DontFindMe%")){
                        return ownerList;
                    }else if(name.equals("%findMe%")){
                        ownerList.add(new Owner(1l,"Kenneth", "Alvarado"));
                        ownerList.add(new Owner(1l,"Juan", "Vargas"));
                        return  ownerList;
                    }

                    throw  new RuntimeException("Invalid argument");
                });
    }

    @Test
    void processFindFormWildCardStringWithAnnotation() {
        //given
        Owner owner = new Owner(1l,"Kenneth", "Alvarado");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);

        //then
        assertThat("%Alvarado%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("redirect:/owners/1").isEqualTo(viewName);



    }

    @Test
    void processFindFormWildCardStringNotFound() {
        //given
        Owner owner = new Owner(1l,"Kenneth", "DontFindMe");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualTo(viewName);

        verifyNoInteractions(model);
    }

    @Test
    void processFindFormWildCardStringFound() {
        //given
        Owner owner = new Owner(1l,"Kenneth", "findMe");
        InOrder inOrder = Mockito.inOrder(ownerService, model);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, Mockito.mock(Model.class));

        //then
        assertThat("%findMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualTo(viewName);

        //inOrder asserts
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        //inOrder.verify(model).addAttribute(anyString(), any());
        verifyNoMoreInteractions(model);
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