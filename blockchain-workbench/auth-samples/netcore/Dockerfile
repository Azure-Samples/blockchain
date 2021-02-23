FROM mcr.microsoft.com/dotnet/core/sdk:2.2

WORKDIR /app

COPY sample.csproj .
RUN dotnet restore

COPY . .
RUN dotnet build --configuration release
RUN dotnet publish --configuration release --output /app

CMD ["dotnet", "/app/sample.dll"]
