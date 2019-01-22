FROM microsoft/dotnet:2.2-sdk

WORKDIR /app

COPY sample.csproj .
RUN dotnet restore

COPY . .
RUN dotnet build --configuration release
RUN dotnet publish --configuration release --output /app

CMD ["dotnet", "/app/sample.dll"]