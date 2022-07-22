namespace PigeOnlineWebAPI
{
    public class Chat
    {
        public int Id { get; set; }
        public string ChatWith { get; set; }

        public List<Message> Messages = new List<Message>();
        public string LastMessage { get; set; }
        public string Date { get; set; }
        public string DisplayName { get; set; }
        public string Image { get; set; }
        public string? ServerURL { get; set; }
        public User chatOwner { get; set; }
    }
}
