package br.com.productmanagementsystem.service;

import br.com.productmanagementsystem.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageServiceTest {

    private MessageService messageService;
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        this.messageSource = mock(MessageSource.class);
        this.messageService = new MessageService(messageSource);
    }

    @Test
    public void givenMessageKey_whenGettingMessage_thenShouldReturnLocalizedMessage() {
        // Arrange
        String messageKey = "product.not.found";
        String expectedMessage = "Product not found";
        Locale currentLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(messageKey), eq(null), eq(currentLocale)))
                .thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey);

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(messageKey, null, currentLocale);
    }

    @Test
    public void givenMessageKeyWithArgs_whenGettingMessage_thenShouldReturnFormattedMessage() {
        // Arrange
        String messageKey = "product.created.success";
        Object[] args = new Object[]{TestConstants.SMARTPHONE_NAME};
        String expectedMessage = "Product 'Smartphone Samsung Galaxy' created successfully";
        Locale currentLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(messageKey), eq(args), eq(currentLocale)))
                .thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey, args);

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(messageKey, args, currentLocale);
    }

    @Test
    public void givenMultipleArgs_whenGettingMessage_thenShouldReturnFormattedMessageWithAllArgs() {
        // Arrange
        String messageKey = "product.update.quantity";
        Object[] args = new Object[]{TestConstants.NOTEBOOK_NAME, TestConstants.NOTEBOOK_QUANTITY, 25};
        String expectedMessage = "Product 'Notebook Dell Inspiron' quantity updated from 20 to 25";
        Locale currentLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(messageKey), eq(args), eq(currentLocale)))
                .thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey, args);

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(messageKey, args, currentLocale);
    }

    @Test
    public void givenDifferentLocale_whenGettingMessage_thenShouldUseCurrentLocale() {
        // Arrange
        String messageKey = "product.created.success";
        Locale enUsLocale = Locale.of("en", "US");
        LocaleContextHolder.setLocale(enUsLocale);
        String expectedMessage = "Product created successfully";

        when(messageSource.getMessage(eq(messageKey), eq(null), eq(enUsLocale)))
                .thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey);

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(messageKey, null, enUsLocale);

        // Clean up - reset locale
        LocaleContextHolder.setLocale(Locale.getDefault());
    }

    @Test
    public void givenEmptyArgs_whenGettingMessage_thenShouldPassEmptyArrayToMessageSource() {
        // Arrange
        String messageKey = "product.list.empty";
        Object[] emptyArgs = new Object[0];
        String expectedMessage = "No products found";
        Locale currentLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(messageKey), eq(emptyArgs), eq(currentLocale)))
                .thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey, emptyArgs);

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(messageKey, emptyArgs, currentLocale);
    }

    @Test
    public void givenNullArg_whenGettingMessage_thenShouldHandleNullInArgsArray() {
        // Arrange
        String messageKey = "product.field.null";
        Object[] args = new Object[]{null, "description"};
        String expectedMessage = "Product field 'description' cannot be null";
        Locale currentLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(messageKey), eq(args), eq(currentLocale))).thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey, args);

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(messageKey, args, currentLocale);
    }

    @Test
    public void givenVarArgs_whenGettingMessage_thenShouldPassAllArgumentsToMessageSource() {
        // Arrange
        String messageKey = "product.price.range";
        String expectedMessage = "Product price must be between 0.01 and 999999.99";
        Locale currentLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(messageKey), any(Object[].class), eq(currentLocale)))
                .thenReturn(expectedMessage);

        // Act
        String actualMessage = this.messageService.getMessage(messageKey, TestConstants.MINIMAL_PRICE.toString(), TestConstants.MAXIMUM_PRICE.toString());

        // Assert
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verify(messageSource).getMessage(eq(messageKey), any(Object[].class), eq(currentLocale));
    }
}