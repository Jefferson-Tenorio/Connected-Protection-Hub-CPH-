package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.CustomerDTO;
import com.assurant.cph.core.domain.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "protectedAssets", ignore = true)
    @Mapping(target = "protectionPlans", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);

    CustomerDTO toDTO(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protectedAssets", ignore = true)
    @Mapping(target = "protectionPlans", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer updateEntityFromDTO(CustomerDTO customerDTO, @org.mapstruct.MappingTarget Customer customer);

    default Customer.Address toAddressEntity(CustomerDTO.AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }

        return Customer.Address.builder()
                .street(addressDTO.getStreet())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .build();
    }

    default CustomerDTO.AddressDTO toAddressDTO(Customer.Address address) {
        if (address == null) {
            return null;
        }

        return CustomerDTO.AddressDTO.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }
}