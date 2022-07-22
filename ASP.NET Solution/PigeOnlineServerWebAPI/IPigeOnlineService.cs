

using Microsoft.AspNetCore.SignalR;
using PigeOnlineWebAPI.Hubs;

namespace PigeOnlineWebAPI;

public interface IPigeOnlineService
{

    // *** Contacts & Chats ***
    Task<Chat> GetChatByUsername(string currentUser, string username);
    Task<List<Chat>> GetChatsByUsername(string currentUser);
    Task<string> AddNewContact(string currentUser, string newUser, string name, string server);
    Task<int> handleInvitation(string from, string to, string server);
    Task<int> UpdateContactByUsername(string currentUser, string id, string server, string name);
    Task<int> DeleteContactByUsername(string currentUser, string username);


    // *** Messages ***
    Task<Message> GetMessageById(int messageID);
    Task<List<Message>> GetMessagesWithContact(string currentUser, string username);
    Task<int> postMessage(string currentUser, string contact, string content, IHubContext<PigeOnlineHub> hubContext);
    Task<int> Transfer(string from, string to, string content);
    Task<int> UpdateMessageById(int messageID, string content);
    Task<int> DeleteMessageById(int messageID);


    // *** Users (Login & Register) ***
    Task<User> GetUser(string id);
    Task<int> PostUser(User user);

    Task<int> InsertConnectionId(string username, string ConnectionId);
    Task<int> DeleteConnectionId(string username);
}
