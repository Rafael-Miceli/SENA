using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebAppSena.Models
{
    public class Client
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public List<Tank> Tanks { get; set; }
    }
}
