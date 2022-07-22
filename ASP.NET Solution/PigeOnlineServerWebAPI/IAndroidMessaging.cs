namespace PigeOnlineWebAPI
{
    public interface IAndroidMessaging
    {
        Task SendNotification(string token, string title, string body);
    }
}
