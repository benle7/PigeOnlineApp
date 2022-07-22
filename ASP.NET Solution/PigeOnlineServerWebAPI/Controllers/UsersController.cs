#nullable disable
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using PigeOnlineWebAPI;
using PigeOnlineWebAPI.Data;

namespace PigeOnlineWebAPI.Controllers
{

    [ApiController]
    public class UsersController : ControllerBase
    {
        private IPigeOnlineService _service;
        private readonly IConfiguration _config;

        public UsersController(IPigeOnlineService service, IConfiguration config)
        {
            _service = service;
            _config = config;
        }


        [Route("api/Users/Login")]
        [HttpPost]
        public async Task<IActionResult> Login(UserValidation userValidation)
        {
            var user = await _service.GetUser(userValidation.Username);

            if (user == null || user.Password != userValidation.Password)
            {
                return NotFound();
            }
            var claims = new[]
           {
                new Claim(JwtRegisteredClaimNames.Sub, _config["JWTParams:Subject"]),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
                new Claim(JwtRegisteredClaimNames.Iat, DateTime.UtcNow.ToString()),
                new Claim("UserId", user.Username)
            };
            var SecretKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["JWTParams:SecretKey"]));
            var mac = new SigningCredentials(SecretKey, SecurityAlgorithms.HmacSha256);
            var token = new JwtSecurityToken(
                _config["JWTParams:Issuer"],
                _config["JWTParams:Audience"],
                claims,
                expires: DateTime.UtcNow.AddMinutes(20),
                signingCredentials: mac);
            // Return the token into username.
            user.Username = new JwtSecurityTokenHandler().WriteToken(token);
            return Ok(user);
        }


        // Register.
        [HttpPost]
        [Route("api/Users")]
        public async Task<IActionResult> PostUser(User user)
        {
            var result = await _service.PostUser(user);
            if(result == 1 || result == 2) {
                return Conflict("false");
            }
            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, _config["JWTParams:Subject"]),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
                new Claim(JwtRegisteredClaimNames.Iat, DateTime.UtcNow.ToString()),
                new Claim("UserId", user.Username)
            };
            var SecretKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["JWTParams:SecretKey"]));
            var mac = new SigningCredentials(SecretKey, SecurityAlgorithms.HmacSha256);
            var token = new JwtSecurityToken(
                _config["JWTParams:Issuer"],
                _config["JWTParams:Audience"],
                claims,
                expires:DateTime.UtcNow.AddMinutes(20),
                signingCredentials:mac);
            return Ok(new JwtSecurityTokenHandler().WriteToken(token));


        }

        [Authorize]
        [Route("api/Users/declare")]
        [HttpPost]
        public async Task<IActionResult> DeclareAndroidOnline(string token)
        {
            string username = this.User.Claims.First(i => i.Type == "UserId").Value;
            await _service.InsertConnectionId(username, token);
            return Ok();
        }

        [Route("api/Users/offline")]
        [HttpPost]
        public async Task<IActionResult> DeleteAndroidOnline(string userName)
        {
            await this._service.DeleteConnectionId(userName);
            return Ok();
        }

        //[HttpGet]
        //public async Task<IActionResult> Logout(User user)
        //{
            // return token with expires 0 
            // delete connection id
        //}

    }
}
