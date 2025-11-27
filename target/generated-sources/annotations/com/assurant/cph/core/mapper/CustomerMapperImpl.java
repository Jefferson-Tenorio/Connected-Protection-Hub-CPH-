package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.CustomerDTO;
import com.assurant.cph.core.domain.Customer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-26T13:39:58-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CustomerDTO customerDTO) {
        if ( customerDTO == null ) {
            return null;
        }

        Customer.CustomerBuilder customer = Customer.builder();

        customer.id( customerDTO.getId() );
        customer.fullName( customerDTO.getFullName() );
        customer.email( customerDTO.getEmail() );
        customer.phoneNumber( customerDTO.getPhoneNumber() );
        customer.documentNumber( customerDTO.getDocumentNumber() );
        customer.documentType( customerDTO.getDocumentType() );
        customer.address( toAddressEntity( customerDTO.getAddress() ) );
        customer.createdAt( customerDTO.getCreatedAt() );
        customer.updatedAt( customerDTO.getUpdatedAt() );

        return customer.build();
    }

    @Override
    public CustomerDTO toDTO(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDTO.CustomerDTOBuilder customerDTO = CustomerDTO.builder();

        customerDTO.id( customer.getId() );
        customerDTO.fullName( customer.getFullName() );
        customerDTO.email( customer.getEmail() );
        customerDTO.phoneNumber( customer.getPhoneNumber() );
        customerDTO.documentNumber( customer.getDocumentNumber() );
        customerDTO.documentType( customer.getDocumentType() );
        customerDTO.address( toAddressDTO( customer.getAddress() ) );
        customerDTO.createdAt( customer.getCreatedAt() );
        customerDTO.updatedAt( customer.getUpdatedAt() );

        return customerDTO.build();
    }

    @Override
    public Customer updateEntityFromDTO(CustomerDTO customerDTO, Customer customer) {
        if ( customerDTO == null ) {
            return customer;
        }

        customer.setFullName( customerDTO.getFullName() );
        customer.setEmail( customerDTO.getEmail() );
        customer.setPhoneNumber( customerDTO.getPhoneNumber() );
        customer.setDocumentNumber( customerDTO.getDocumentNumber() );
        customer.setDocumentType( customerDTO.getDocumentType() );
        customer.setAddress( toAddressEntity( customerDTO.getAddress() ) );

        return customer;
    }
}
