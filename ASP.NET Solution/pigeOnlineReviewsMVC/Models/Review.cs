using System.ComponentModel.DataAnnotations;

namespace pigeOnline.Models
{
    public class Review
    {   
        public int Id { get; set; }

        [Required]
        public string Name { get; set; }

        [Required]
        [Range(1,5, ErrorMessage = "Rate must be between 1 to 5.")]
        public int RateNumber { get; set; }

        public string? Text { get; set; }

        public string? TimeAndDate { get; set; }
    }


}
