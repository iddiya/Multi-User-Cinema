package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.dtos.PaymentCardDto;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.PaymentCardForm;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.PaymentCardService;
import com.ecinema.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaymentCardControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private PaymentCardService paymentCardService;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private UserService userService;

    @MockBean
    private InitializationConfig config;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showPaymentCardsPage()
        throws Exception {
        setUpCustomer();
        List<PaymentCardDto> paymentCards = new ArrayList<>() {{
            add(new PaymentCardDto());
            add(new PaymentCardDto());
        }};
        given(paymentCardService.findAllByCardUserWithId(1L))
                .willReturn(paymentCards);
        mockMvc.perform(get("/payment-cards"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("paymentCards", paymentCards));
    }

    @Test
    @WithAnonymousUser
    void failToShowPaymentCardsPage1()
        throws Exception {
        mockMvc.perform(get("/payment-cards"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"MODERATOR", "ADMIN"})
    void failToShowPaymentCardsPage2()
            throws Exception {
        mockMvc.perform(get("/payment-cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showAddPaymentCardPage()
            throws Exception {
        setUpCustomer();
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        mockMvc.perform(get("/add-payment-card"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("paymentCardForm", paymentCardForm));
    }

    @Test
    @WithAnonymousUser
    void failToShowAddPaymentCardPage1()
        throws Exception {
        mockMvc.perform(get("/add-payment-card"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"MODERATOR", "ADMIN"})
    void failToShowAddPaymentCardPage2()
        throws Exception {
        mockMvc.perform(get("/add-payment-card"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void addPaymentCard()
        throws Exception {
        setUpCustomer();
        doNothing().when(paymentCardService)
                   .submitPaymentCardFormToAddNewPaymentCard(any(PaymentCardForm.class));
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        mockMvc.perform(post("/add-payment-card")
                                .flashAttr("paymentCardForm", paymentCardForm))
                .andExpect(redirectedUrlPattern("/payment-cards**"))
                .andExpect(result -> model().attributeExists("success"));
    }

    @Test
    @WithAnonymousUser
    void failToAddPaymentCard1()
        throws Exception {
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        mockMvc.perform(post("/add-payment-card")
                                .flashAttr("paymentCardForm", paymentCardForm))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"MODERATOR", "ADMIN"})
    void failToAddPaymentCard2()
        throws Exception {
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        mockMvc.perform(post("/add-payment-card")
                                .flashAttr("paymentCardForm", paymentCardForm))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToAddPaymentCard3()
        throws Exception {
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        paymentCardForm.setFirstName("Johnny");
        NoEntityFoundException e = new NoEntityFoundException("payment card", "id", 1L);
        doThrow(e).when(paymentCardService)
                  .submitPaymentCardFormToAddNewPaymentCard(paymentCardForm);
        mockMvc.perform(post("/add-payment-card")
                                .flashAttr("paymentCardForm", paymentCardForm))
                .andExpect(redirectedUrlPattern("/add-payment-card**"))
                .andExpect(result -> model().attribute("paymentCardForm", paymentCardForm))
                .andExpect(result -> model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showEditPaymentCardPage()
            throws Exception {
        setUpCustomer();
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        paymentCardForm.setPaymentCardId(1L);
        given(paymentCardService.fetchAsForm(1L)).willReturn(paymentCardForm);
        mockMvc.perform(get("/edit-payment-card")
                                .param("id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("paymentCardForm", paymentCardForm));
    }

    @Test
    @WithAnonymousUser
    void failToShowEditPaymentCardPage1()
        throws Exception {
        mockMvc.perform(get("/edit-payment-card")
                                .param("id", String.valueOf(1L)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN", "MODERATOR"})
    void failToShowEditPaymentCardPage2()
        throws Exception {
        mockMvc.perform(get("/edit-payment-card")
                                .param("id", String.valueOf(1L)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToShowEditPaymentCardPage3()
        throws Exception {
        setUpCustomer();
        NoEntityFoundException e = new NoEntityFoundException("payment card", "id", 1L);
        doThrow(e).when(paymentCardService).fetchAsForm(1L);
        mockMvc.perform(get("/edit-payment-card")
                                .param("id", String.valueOf(1L)))
                .andExpect(redirectedUrlPattern("/payment-cards**"))
                .andExpect(result -> model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void editPaymentCard()
        throws Exception {
        setUpCustomer();
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        doNothing().when(paymentCardService)
                   .submitPaymentCardFormToEditPaymentCard(any(PaymentCardForm.class));
        mockMvc.perform(post("/edit-payment-card/" + 1L)
                                .flashAttr("paymentCardForm", paymentCardForm))
                .andExpect(redirectedUrlPattern("/payment-cards**"))
                .andExpect(result -> model().attributeExists("success"));
    }


    @Test
    @WithAnonymousUser
    void failToEditPaymentCard1()
            throws Exception {
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        mockMvc.perform(post("/edit-payment-card/" + 1L)
                                .flashAttr("paymentCardForm", paymentCardForm))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"MODERATOR", "ADMIN"})
    void failToEditPaymentCard2()
            throws Exception {
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        mockMvc.perform(post("/edit-payment-card/" + 1L)
                                .flashAttr("paymentCardForm", paymentCardForm))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToEditPaymentCard3()
            throws Exception {
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        paymentCardForm.setFirstName("Johnny");
        NoEntityFoundException e = new NoEntityFoundException("payment card", "id", 1L);
        doThrow(e).when(paymentCardService)
                  .submitPaymentCardFormToEditPaymentCard(paymentCardForm);
        mockMvc.perform(post("/edit-payment-card/" + 1L)
                                .flashAttr("paymentCardForm", paymentCardForm))
               .andExpect(redirectedUrlPattern("/edit-payment-card**"))
               .andExpect(result -> model().attribute("paymentCardForm", paymentCardForm))
               .andExpect(result -> model().attributeExists("errors"));
    }

    private void setUpCustomer() {
        given(securityContext.findIdOfLoggedInUser()).willReturn(1L);
        UserDto userDto = new UserDto();
        userDto.getUserAuthorities().add(UserAuthority.CUSTOMER);
        given(userService.findById(1L)).willReturn(userDto);
    }

}