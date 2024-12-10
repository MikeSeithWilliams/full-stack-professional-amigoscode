package com.seith_amigoscode.customer;

import com.seith_amigoscode.exception.DuplicateResourceException;
import com.seith_amigoscode.exception.RequestValidationException;
import com.seith_amigoscode.exception.ResourceNotFoundException;
import com.seith_amigoscode.s3.S3Buckets;
import com.seith_amigoscode.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(
                customerDao,
                passwordEncoder,
                customerDTOMapper,
                s3Service,
                s3Buckets
        );
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);


        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        // Given
        long id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When

        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerByEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );

        String passwordHash = "weg43wef)(+Oawr";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerByEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");
        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        long id = 10;

        when(customerDao.existsCustomerById(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        // Given
        long id = 10;

        when(customerDao.existsCustomerById(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void updateAllCustomersProperties() {
        // Given
        long id = 10;
        String email = "alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", email, 23, Gender.MALE
        );
        Customer customer = new Customer(
                id, "Alexandra", "alexandra@gmail.com", "password", 19, Gender.FEMALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerByEmail(email)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(updateRequest.gender());
    }

    @Test
    void updateOnlyCustomerName() {
        // Given
        long id = 10;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", null, null, null
        );
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateOnlyCustomerEmail() {
        // Given
        long id = 10;
        String email = "alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, email, null, null
        );
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerByEmail(email)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateOnlyCustomerAge() {
        // Given
        long id = 10;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 20, null
        );
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateOnlyCustomerGender() {
        // Given
        long id = 10;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, null, Gender.FEMALE
        );
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(updateRequest.gender());
    }

    @Test
    void WillThrowWhenTryingToUpdateOnlyCustomerEmailWhenAlreadyTaken() {
        // Given
        long id = 10;
        String email = "alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, email, null, null
        );
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerByEmail(email)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        long id = 10;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alex", "alex@gmail.com", 19, Gender.MALE
        );
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void canUploadProfileImage() {
        // Given
        long customerId = 10;
        when(customerDao.existsCustomerById(customerId)).thenReturn(true);
        byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", bytes);
        String bucket = "customer-bucket";

        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        underTest.uploadCustomerProfileImage(customerId, multipartFile);

        // Then
        ArgumentCaptor<String> profileImageIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(customerDao).updateCustomerProfileId(
                profileImageIdArgumentCaptor.capture(),
                eq(customerId)
        );
        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageIdArgumentCaptor.getValue()),
                bytes
        );
    }

    @Test
    void canNotUploadProfileImageWhenCustomerDoesNotExist() {
        // Given
        long customerId = 10;
        when(customerDao.existsCustomerById(customerId)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(
                customerId, mock(MultipartFile.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id ["+customerId+"] not found");


        // Then
        verify(customerDao).existsCustomerById(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void canNotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        // Given
        long customerId = 10;
        when(customerDao.existsCustomerById(customerId)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(customerId, multipartFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to upload profile image")
                .hasRootCauseInstanceOf(IOException.class);

        // Then
        verify(customerDao, never()).updateCustomerProfileId(any(), any());
    }

    @Test
    void canDownloadProfileImage() {
        // Given
        long customerId = 10;
        String profileImageId = "22222";
        Customer customer = new Customer(
                customerId, "Alex", "alex@gmail.com", "password", 19, Gender.MALE, profileImageId
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();
        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void canNotDownloadWhenNoProfileImageId() {
        // Given
        long customerId = 10;
        Customer customer = new Customer(
                customerId, "Alex", "alex@gmail.com", "password", 19, Gender.MALE
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] profile image not found".formatted(customerId));
        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);
    }

    @Test
    void canNotDownloadProfileImageWhenCustomerDoesNotExist() {
        // Given
        long customerId = 10;
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(customerId));
        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);
    }
}