package java.com.socialmedia;

import com.socialmedia.dao.MessageDAO;
import com.socialmedia.exception.DatabaseException;
import com.socialmedia.exception.MessageTooLongException;
import com.socialmedia.model.Message;
import com.socialmedia.observer.MessagePublisher;
import com.socialmedia.service.MessageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


class MessageServiceTest {

    @Mock
    private MessageDAO messageDAO;

    @Mock
    private MessagePublisher messagePublisher;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // override final fields via subclass trick for testing
        messageService = new MessageService() {
            {
                this.messageDAO = MessageServiceTest.this.messageDAO;
                this.messagePublisher = MessageServiceTest.this.messagePublisher;
            }
        };
    }

    @Test
    void postMessage_successful() throws Exception {
        int channelId = 1;
        int userId = 42;
        String content = "Hello World!";
        Message message = new Message(channelId, userId, content);
        message.setId(123);

        when(messageDAO.createMessage(any())).thenReturn(message);

        Message result = messageService.postMessage(channelId, userId, content);

        assertNotNull(result);
        assertEquals(123, result.getId());
        verify(messageDAO).createMessage(any(Message.class));
        verify(messagePublisher).publishMessage(any(Message.class));
    }

    @Test
    void postMessage_nullContent_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
            messageService.postMessage(1, 1, null)
        );
    }

    @Test
    void postMessage_emptyContent_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
            messageService.postMessage(1, 1, "   ")
        );
    }

    @Test
    void postMessage_tooLong_throwsMessageTooLongException() {
        String longContent = "a".repeat(Message.MAX_CONTENT_LENGTH + 1);

        assertThrows(MessageTooLongException.class, () ->
            messageService.postMessage(1, 1, longContent)
        );
    }

    @Test
    void postMessage_databaseError_throwsDatabaseException() throws Exception {
        when(messageDAO.createMessage(any())).thenThrow(new DatabaseException("DB failure"));

        assertThrows(DatabaseException.class, () ->
            messageService.postMessage(1, 1, "Valid message")
        );
    }

    @Test
    void getMessagesForChannel_return_
