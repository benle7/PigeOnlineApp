#nullable disable
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using PigeOnlineWebAPI;
using PigeOnlineWebAPI.Data;


namespace PigeOnlineWebAPI.Controllers
{

    [ApiController]
    public class ChatsController : ControllerBase
    {
        private IPigeOnlineService _service;
        private readonly IConfiguration _config;

        public ChatsController(IConfiguration config, IPigeOnlineService service)
        {
            _service = service;
            _config = config;
            
        }

        // api/contacts - to get all chats of currrent user.
        [Authorize]
        [HttpGet]
        [Route("api/contacts")]
        public async Task<ActionResult<List<Chat>>> GetChats()
        {
            string username = this.User.Claims.First(i => i.Type == "UserId").Value;

            List<Chat> result =  await  _service.GetChatsByUsername(username);
            if(result == null)
            {
                return NotFound();
            }
            return result;
        }

        // api/contacts/id - to get specific chat of currrent user with id.
        [Authorize]
        [HttpGet]
        [Route("api/contacts/{id}")]
        public async Task<ActionResult<Chat>> GetChat(string id)
        {
            string username = this.User.Claims.First(i => i.Type == "UserId").Value;

            Chat chat = await _service.GetChatByUsername(username, id);
            if (chat == null)
            {
                return NotFound();
            }
            return chat;
        }



        // api/contacts - to add new contact to current user.
        [Authorize]
        [HttpPost]
        [Route("api/contacts")]
        public async Task<ActionResult> PostChat(PostContactParams details)
        {
            string result;
            string currentUser = this.User.Claims.First(i => i.Type == "UserId").Value;

            result = await _service.AddNewContact(currentUser, details.Id, details.Name, details.Server);

            if(result == null)
            {
                return NoContent();
            }


            return Created("getPathHere", result);
        }

        // api/invitations - to get invitation from another user that add me.
        [Route("api/invitations")]
        [HttpPost]
        public async Task<ActionResult> getInvitation(InvitationParams details)
        {
            int result = 1;
            result = await _service.handleInvitation(details.From, details.To, details.Server);

            if (result == 1)
            {
                return NoContent();
            }
            return StatusCode(201);
        }
        


        [HttpDelete]
        [Route("api/contacts/{id}")]
        public async Task<IActionResult> DeleteChat(string id)
        {
            string currentUser = this.User.Claims.First(i => i.Type == "UserId").Value;
            var result = await _service.DeleteContactByUsername(currentUser, id);
            if (result == 1)
            {
                return NotFound();
            }

            return StatusCode(204);
        }

        [HttpPut]
        [Route("api/contacts/{id}")]
        public async Task<IActionResult> updateChat(PutContactParams details, string id)
        {
            string currentUser = this.User.Claims.First(i => i.Type == "UserId").Value;
            var result = await _service.UpdateContactByUsername(currentUser, id, details.Server, details.Name);
            if (result == 1)
            {
                return NotFound();
            }

            return StatusCode(204);
        }

    }
}
