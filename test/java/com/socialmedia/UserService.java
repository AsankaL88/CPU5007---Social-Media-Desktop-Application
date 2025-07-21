package java.com.socialmedia;

import main.java.com.socialmedia.dao.UserDAO;
import com.socialmedia.exception.DatabaseException;
import com.socialmedia.exception.InvalidCredentialsException;
import main.java.com.socialmedia.exception.UserAlreadyExistsException;
import main.java.com.socialmedia.model.User;
import main.java.com.socialmedia.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // manually inject mock DAO because your constructor creates new DAO internally
        userService = new UserService() {
            {
                // Override with mock DAO
                this.userDAO = UserServiceTest.this.userDAO;
            }
        };
    }

    @Test
    void registerUser_success() throws Exception {
        String email = "user@example.com";
        String password = "securePassword";

        User createdUser = new User(email, "salt:hash");
        when(userDAO.createUser(any(User.class))).thenReturn(createdUser);

        User result = userService.registerUser(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userDAO).createUser(any(User.class));
    }

    @Test
    void registerUser_emptyEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("   ", "pass")
        );
    }

    @Test
    void registerUser_emptyPassword_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("user@example.com", "  ")
        );
    }

    @Test
    void authenticateUser_success() throws Exception {
        String email = "test@example.com";
        String password = "mypassword";

        // simulate hashed password
        User user = new User(email, userService.registerUser(email, password).getPassword());
        when(userDAO.findUserByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.authenticateUser(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void authenticateUser_invalidEmail_throwsException() {
        assertThrows(InvalidCredentialsException.class, () ->
            userService.authenticateUser(" ", "pass")
        );
    }

    @Test
    void authenticateUser_invalidPassword_throwsException() {
        assertThrows(InvalidCredentialsException.class, () ->
            userService.authenticateUser("user@example.com", "")
        );
    }

    @Test
    void authenticateUser_userNotFound_throwsException() {
        when(userDAO.findUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () ->
            userService.authenticateUser("test@example.com", "pass")
        );
    }

    @Test
    void authenticateUser_wrongPassword_throwsException() throws Exception {
        String email = "user@example.com";
        String password = "correct";
        String wrongPassword = "wrong";

        // simulate hashed password
        String storedPassword = userService.registerUser(email, password).getPassword();
        User user = new User(email, storedPassword);

        when(userDAO.findUserByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () ->
            userService.authenticateUser(email, wrongPassword)
        );
    }

    @Test
    void userExists_returnsTrueIfExists() throws Exception {
        when(userDAO.existsByEmail("user@example.com")).thenReturn(true);

        assertTrue(userService.userExists("user@example.com"));
    }

    @Test
    void userExists_returnsFalseIfNullOrEmpty() throws Exception {
        assertFalse(userService.userExists(null));
        assertFalse(userService.userExists("  "));
    }

    @Test
    void findUserById_returnsUserIfExists() throws Exception {
        User user = new User("x@y.com", "pass");
        when(userDAO.findUserById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(1);

        assertTrue(result.isPresent());
        assertEquals("x@y.com", result.get().getEmail());
    }
}
