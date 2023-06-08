using Devon4Net.Infrastructure.Common.Exceptions;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.Text;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Exceptions
{
    /// <summary>
    /// Custom exception ${variables.entityName?cap_first}NotFoundException
    /// </summary>
    [Serializable]
    public class ${variables.entityName?cap_first}NotFoundException : Exception, IWebApiException
    {
        /// <summary>
        /// The forced http status code to be fired on the exception manager
        /// </summary>
        public int StatusCode => StatusCodes.Status404NotFound;

        /// <summary>
        /// Show the message on the response?
        /// </summary>
        public bool ShowMessage => true;

        /// <summary>
        /// Initializes a new instance of the <see cref="${variables.entityName?cap_first}NotFoundException"/> class.
        /// </summary>
        public ${variables.entityName?cap_first}NotFoundException()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="${variables.entityName?cap_first}NotFoundException"/> class.
        /// </summary>
        /// <param name="message">The message that describes the error.</param>
        public ${variables.entityName?cap_first}NotFoundException(string message)
            : base(message)
        {
        }
    }
}
