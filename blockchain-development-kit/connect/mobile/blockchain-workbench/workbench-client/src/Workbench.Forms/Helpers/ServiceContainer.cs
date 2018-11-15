using System;
using System.Threading;
using System.Collections.Generic;

namespace Workbench.Forms.Helpers
{
    public static class ServiceContainer
    {
        static readonly Dictionary<Type, Lazy<object>> services = new Dictionary<Type, Lazy<object>>();

        /// <summary>
        /// Register the specified service with an instance
        /// </summary>
        public static void Register<T>(T service)
        {
            services[typeof(T)] = new Lazy<object>(() => service);
        }

        /// <summary>
        /// Register the specified service for a class with a default constructor
        /// </summary>
        public static void Register<T>() where T : new()
        {
            services[typeof(T)] = new Lazy<object>(() => new T(), LazyThreadSafetyMode.ExecutionAndPublication);
        }

        /// <summary>
        /// Register the specified service with a callback to be invoked when requested
        /// </summary>
        public static void Register<T>(Func<T> function)
        {
            services[typeof(T)] = new Lazy<object>(() => function());
        }

        /// <summary>
        /// Register the specified service with an instance
        /// </summary>
        public static void Register(Type type, object service)
        {
            services[type] = new Lazy<object>(() => service);
        }

        /// <summary>
        /// Register the specified service with a callback to be invoked when requested
        /// </summary>
        public static void Register(Type type, Func<object> function)
        {
            services[type] = new Lazy<object>(function, LazyThreadSafetyMode.ExecutionAndPublication);
        }

        /// <summary>
        /// Resolves the type, throwing an exception if not found
        /// </summary>
        public static T Resolve<T>(bool nullIsAcceptable = false)
        {
            return (T)Resolve(typeof(T), nullIsAcceptable);
        }

        /// <summary>
        /// Resolves the type, throwing an exception if not found
        /// </summary>
        public static object Resolve(Type type, bool nullIsAcceptable = false)
        {
            //Non-scoped services
            {
                Lazy<object> service;
                if (services.TryGetValue(type, out service))
                    return service.Value;

                if (nullIsAcceptable) return null;

                throw new KeyNotFoundException(string.Format("Service not found for type '{0}'", type));
            }
        }

        /// <summary>
        /// Clears the entire container
        /// </summary>
        public static void Clear()
        {
            services.Clear();
        }
    }
}