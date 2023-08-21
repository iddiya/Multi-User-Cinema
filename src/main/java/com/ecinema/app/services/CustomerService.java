package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.CustomerDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.CustomerRepository;
import com.ecinema.app.repositories.ScreeningSeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for {@link Customer}.
 */
@Service
@Transactional
public class CustomerService extends UserAuthorityService<Customer, CustomerRepository, CustomerDto> {

    private final EmailService emailService;
    private final ReviewService reviewService;
    private final TicketService ticketService;
    private final SecurityContext securityContext;
    private final ReviewVoteService reviewVoteService;
    private final PaymentCardService paymentCardService;
    private final ScreeningSeatRepository screeningSeatRepository;

    /**
     * Instantiates a new Customer service.
     *
     * @param repository              See {@link CustomerRepository}
     * @param screeningSeatRepository See {@link ScreeningSeatRepository}
     * @param emailService            See {@link EmailService}
     * @param reviewService           See {@link ReviewService}
     * @param ticketService           See {@link TicketService}
     * @param paymentCardService      See {@link PaymentCardService}
     * @param reviewVoteService       the review vote service
     * @param securityContext         See {@link SecurityContext}
     */
    @Autowired
    public CustomerService(CustomerRepository repository, ScreeningSeatRepository screeningSeatRepository,
                           EmailService emailService, ReviewService reviewService, TicketService ticketService,
                           PaymentCardService paymentCardService, ReviewVoteService reviewVoteService,
                           SecurityContext securityContext) {
        super(repository);
        this.emailService = emailService;
        this.ticketService = ticketService;
        this.reviewService = reviewService;
        this.securityContext = securityContext;
        this.reviewVoteService = reviewVoteService;
        this.paymentCardService = paymentCardService;
        this.screeningSeatRepository = screeningSeatRepository;
    }

    @Override
    protected void onDelete(Customer customer) {
        logger.debug("Customer on delete");
        // detach User
        super.onDelete(customer);
        // cascade delete Reviews
        logger.debug("Deleting all associated reviews");
        reviewService.deleteAll(customer.getReviews());
        // cascade delete Review Votes
        logger.debug("Deleting all associated review votes");
        reviewVoteService.deleteAll(customer.getReviewVotes());
        // cascade delete Tickets
        logger.debug("Deleting all associated tickets");
        ticketService.deleteAll(customer.getTickets());
        // cascade delete PaymentCards
        logger.debug("Deleting all associated payment cards");
        paymentCardService.deleteAll(customer.getPaymentCards());
        // detach Moderator censors
        Moderator moderator = customer.getCensoredBy();
        logger.debug("Detaching moderator role def: " + moderator);
        if (moderator != null) {
            customer.setCensoredBy(null);
            moderator.getCensoredCustomers().remove(customer);
        }
    }

    @Override
    public CustomerDto convertToDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        User user = customer.getUser();
        if (user != null) {
            customerDto.setUserId(user.getId());
            customerDto.setEmail(user.getEmail());
            customerDto.setUsername(user.getUsername());
        }
        Moderator censor = customer.getCensoredBy();
        if (censor != null) {
            customerDto.setIsCensored(censor != null);
            customerDto.setCensorId(censor != null ? censor.getId() : null);
        }
        logger.debug("Converted " + customer + " to DTO: " + customerDto);
        return customerDto;
    }

    public Integer numberOfTokensOwnedByUser(Long userId)
            throws NoEntityFoundException  {
        return repository.numberOfTokensOwnedByUserId(userId).orElseThrow(
                () -> new NoEntityFoundException("customer tokens", "user id", userId));
    }

    public boolean isCustomerCensored(Long userId)
        throws NoEntityFoundException {
        Customer customer = repository.findByUserWithId(userId)
                                      .orElseThrow(() -> new NoEntityFoundException(
                                              "customer authority", "user id", userId));
        return customer.getCensoredBy() != null;
    }

}
