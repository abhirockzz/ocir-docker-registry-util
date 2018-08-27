package com.oracle.ocir.util;

import com.google.gson.Gson;

import com.oracle.ocir.util.pojo.RepoTagsList;
import com.oracle.ocir.util.pojo.Repositories;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class OCIRCleanUtil {

    static String OCIR_DOCKERV2_API_ENDPOINT;
    static Client client = null;

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: java -jar ocir-util-1.0.0.jar  <path_to_properties_config_file>");
            return;
        }

        String propertiesFileLocation = args[0];
        System.out.println("Configuration properties file location " + propertiesFileLocation);

        Properties props = new Properties();
        props.load(new FileInputStream(propertiesFileLocation));

        String ocirRegistry = props.getProperty("ocir_registry");
        System.out.println("OCIR registry " + ocirRegistry);

        OCIR_DOCKERV2_API_ENDPOINT = "https://" + ocirRegistry + "/v2/";
        System.out.println("OCIR Docker V2 endpoint " + OCIR_DOCKERV2_API_ENDPOINT);

        String prefixForImagesToBeDeleted = props.getProperty("image_prefix_for_deletion");
        System.out.println("Prefix for Images to be delted " + prefixForImagesToBeDeleted);

        String ocirDockerV2APIAccessToken = props.getProperty("ocir_docker_v2_api_access_token");

        client = ClientBuilder
                .newBuilder()
                .build();

        //client = ClientBuilder.newBuilder().build();
        String repos = client.target(OCIR_DOCKERV2_API_ENDPOINT)
                .path("_catalog")
                .request()
                .header("Authorization", "Bearer " + ocirDockerV2APIAccessToken)
                .get(String.class);
        Repositories reposJ = new Gson().fromJson(repos, Repositories.class);

        List<String> tobeDeleted = reposJ.getRepositories().stream()
                .map((r) -> r.split("/")[1]) //e.g. odx-jafar/abhishek/test-app/my-func:0.0.1
                .filter((r) -> r.startsWith(prefixForImagesToBeDeleted))
                .collect(Collectors.toList());

        //System.out.println("Listing images...........");
        if (tobeDeleted.isEmpty()) {
            System.out.println("No repositories to be deleted");
            return;
        }
        
        for (String repo : tobeDeleted) {
            System.out.println("Repo to be deleted -- "+ repo);
        }

        for (String repo : tobeDeleted) {

            System.out.println("Images in " + repo + " will be DELETED. Enter yes to proceed, else the process will be terminated");
            Scanner prompt = new Scanner(System.in);
            String yesOrNo = prompt.nextLine();

            if (!yesOrNo.equalsIgnoreCase("yes")) {
                System.out.println("Image deletion process will NOT proceed further");
                continue;
            }
            //get tags for repo
            String tags = client.target(OCIR_DOCKERV2_API_ENDPOINT)
                    .path(repo + "/tags/list")
                    .request()
                    .header("Authorization", "Bearer " + ocirDockerV2APIAccessToken)
                    .get(String.class);

            RepoTagsList tagsJ = new Gson().fromJson(tags, RepoTagsList.class);
            System.out.println("Getting tags for repo " + repo);
            for (String tag : tagsJ.getTags()) {
                //System.out.println("Tag " + tag);
                //System.out.println("Getting manifest for tag " + tag);

                //get digest for tag
                Response manifestResp = client.target(OCIR_DOCKERV2_API_ENDPOINT)
                        .path(repo + "/manifests/" + tag)
                        .request()
                        .header("Authorization", "Bearer " + ocirDockerV2APIAccessToken)
                        .header("Accept", "application/vnd.docker.distribution.manifest.v2+json")
                        .get();

                String digest = manifestResp.getHeaderString("Docker-Content-Digest");
                System.out.println("Digest for tag " + tag + " in repo " + " repo " + digest);

                //DELETing image using digest
                Response deleteResponse = client.target(OCIR_DOCKERV2_API_ENDPOINT)
                        .path(repo + "/manifests/" + digest)
                        .request()
                        .header("Authorization", "Bearer " + ocirDockerV2APIAccessToken)
                        .delete();

                System.out.println("Deletion status for tag " + tag + " in repo " + " repo " + deleteResponse.getStatus());
            }
        }
    }
}
