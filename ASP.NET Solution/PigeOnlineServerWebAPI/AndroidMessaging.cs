namespace PigeOnlineWebAPI

{
    using FirebaseAdmin;
    using FirebaseAdmin.Messaging;
    using Google.Apis.Auth.OAuth2;
    using System.Threading.Tasks;

    public class AndroidMessaging : IAndroidMessaging
    {
        private readonly FirebaseMessaging messaging;

        public AndroidMessaging()
        {
            var app = FirebaseApp.Create(new AppOptions() { Credential = GoogleCredential.FromFile("FireBaseKey.json").CreateScoped("https://www.googleapis.com/auth/firebase.messaging") });
            this.messaging = FirebaseMessaging.GetMessaging(app);
        }

        private FirebaseAdmin.Messaging.Message CreateAndroidMessage(string title, string body, string token)
        {
            var message = new FirebaseAdmin.Messaging.Message();
            message.Token = token;
            message.Notification = new FirebaseAdmin.Messaging.Notification();
            message.Notification.Body = body;
            message.Notification.Title = title;
            return message;
        }

        public async Task SendNotification(string token, string title, string body)
        {
            if(token != null)
            {
                await messaging.SendAsync(CreateAndroidMessage(title, body, token));
            } 
        }
    }
}
