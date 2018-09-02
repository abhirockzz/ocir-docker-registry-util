package com.oracle.ocir.util;

import com.google.gson.Gson;
import com.oracle.ocir.util.pojo.Repos;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class OCIRCleanUtil {

    static Client client = null;
    static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: java -jar ocir-util-1.0.0.jar  <path_to_properties_config_file>");
            return;
        }

        String propertiesFileLocation = args[0];
        System.out.println("Configuration properties file location " + propertiesFileLocation);

        Properties props = new Properties();
        props.load(new FileInputStream(propertiesFileLocation));

        String ocirRegistryBaseEndpoint = props.getProperty("ocir_registry_base");
        System.out.println("OCIR registry " + ocirRegistryBaseEndpoint);

        String ociTenancyName = props.getProperty("oci_tenancy_name");
        System.out.println("OCI tenancy name " + ociTenancyName);

        String prefixForImagesToBeDeleted = props.getProperty("image_prefix_for_deletion");
        System.out.println("Prefix for Images to be deleted " + prefixForImagesToBeDeleted);

        String ocirDockerV2APIAccessToken = props.getProperty("ocir_docker_v2_api_access_token");

        client = ClientBuilder
                .newBuilder()
                .build();

        //client = ClientBuilder.newBuilder().build();
        String reposJSON = client.target("https://" + ocirRegistryBaseEndpoint)
                .path("/docker/repos/" + ociTenancyName)
                .request()
                .header("Authorization", "Bearer " + ocirDockerV2APIAccessToken)
                .get(String.class);
        Repos repos = gson.fromJson(reposJSON, Repos.class);

        List<String> tobeDeleted = repos.getRepos().stream()
                .map((r) -> r.getRepoPath()) //e.g. odx-jafar/abhishek/my-func:0.0.1
                //.filter((r) -> r.split("/")[1].startsWith(prefixForImagesToBeDeleted))
                .filter(r -> r.substring(r.indexOf("/")+1, r.length()).startsWith(prefixForImagesToBeDeleted))
                .collect(Collectors.toList());

        if (tobeDeleted.isEmpty()) {
            System.out.println("No repositories to be deleted");
            return;
        }

        System.out.println("Listing images...........");
        for (String repo : tobeDeleted) {
            System.out.println(repo);
        }

        System.out.println("Above mentioned repos and their images will be DELETED. Enter yes to proceed, else the process will be terminated");
        Scanner prompt = new Scanner(System.in);
        String yesOrNo = prompt.nextLine();

        if (!yesOrNo.equalsIgnoreCase("yes")) {
            System.out.println("Deletion process will NOT proceed further");
            return;
        }

        for (String repo : tobeDeleted) {
            //invoke delete
            int delStatus = client.target("https://" + ocirRegistryBaseEndpoint)
                    .path("/docker/repos/" + repo)
                    .request()
                    .header("Authorization", "Bearer " + ocirDockerV2APIAccessToken)
                    .delete()
                    .getStatus();

            String delMsg = (delStatus == 204) ? repo + " deleted successfully" : "Could not delete repo " + repo;
            System.out.println(delMsg);
        }
    }
}
