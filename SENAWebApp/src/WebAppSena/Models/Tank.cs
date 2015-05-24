using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebAppSena.Models
{
    public class Tank
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public int? LastLevel { get; set; }
        public int? CriticalLevel { get; set; }
        public double? Height { get; set; }
    }
}
