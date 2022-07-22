namespace PigeOnlineWebAPI
{
    public class Message
    {
        public int Id { get; set; }
        public string From { get; set; }
        public string Content { get; set; }
        public string Type { get; set; }
        public string Date { get; set; }
        public string SenderPicture { get; set; }

        public int? chatOwnerId { get; set; }

    }
}
