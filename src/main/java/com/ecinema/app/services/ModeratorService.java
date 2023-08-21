package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ModeratorDto;
import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Moderator;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.CustomerRepository;
import com.ecinema.app.repositories.ModeratorRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;

@Service
@Transactional
public class ModeratorService extends UserAuthorityService<Moderator, ModeratorRepository, ModeratorDto> {

    private final CustomerRepository customerRepository;

    @Autowired
    public ModeratorService(ModeratorRepository repository, CustomerRepository customerRepository) {
        super(repository);
        this.customerRepository = customerRepository;
    }

    @Override
    protected void onDelete(Moderator moderator) {
        // detach User
        super.onDelete(moderator);
        // uncensor and detach censored Customers
        logger.debug("Detach moderator from customers censored by moderator");
        Iterator<Customer> customerRoleDefIterator = moderator.getCensoredCustomers().iterator();
        while (customerRoleDefIterator.hasNext()) {
            Customer customer = customerRoleDefIterator.next();
            logger.debug("Detaching: " + customer);
            customer.setCensoredBy(null);
            customerRoleDefIterator.remove();
        }
    }

    @Override
    public ModeratorDto convertToDto(Moderator moderator) {
        ModeratorDto moderatorDto = new ModeratorDto();
        fillCommonUserAuthorityDtoFields(moderator, moderatorDto);
        return moderatorDto;
    }

    public void setCustomerCensoredStatus(Long moderatorRoleDefId, Long customerRoleDefId, boolean censor)
            throws NoEntityFoundException, ClashException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Set customer censored status");
        Moderator moderator = repository.findById(moderatorRoleDefId).orElseThrow(
                () -> new NoEntityFoundException("moderator", "id", moderatorRoleDefId));
        logger.debug("Found moderator by id: " + moderator);
        Customer customer = customerRepository.findById(customerRoleDefId).orElseThrow(
                () -> new NoEntityFoundException("customer", "id", customerRoleDefId));
        logger.debug("Found customer by id: " + customer);
        logger.debug("Setting censored status to " + censor);
        customer.setCensoredBy(censor ? moderator : null);
        logger.debug("Saved customer: " + customer);
        customerRepository.save(customer);
    }

}
