package com.fastaccess.data.service;

import com.fastaccess.data.dao.AssigneesRequestModel;
import com.fastaccess.data.dao.CommitModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.MergeRequestModel;
import com.fastaccess.data.dao.MergeResponseModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.data.dao.RepoFilesModel;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Kosh on 15 Dec 2016, 10:21 PM
 */

public interface PullRequestService {

    @GET("repos/{owner}/{repo}/pulls")
    Observable<Pageable<PullRequestModel>> getPullRequests(@Path("owner") String owner, @Path("repo") String repo,
                                                           @Query("state") String state, @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/{number}")
    Observable<PullRequestModel> getPullRequest(@Path("owner") String owner, @Path("repo") String repo, @Path("number") long number);

    @PUT("repos/{owner}/{repo}/pulls/{number}/merge")
    Observable<MergeResponseModel> mergePullRequest(@Path("owner") String owner, @Path("repo") String repo,
                                                    @Path("number") long number, @Body MergeRequestModel body);


    @GET("repos/{owner}/{repo}/pulls/{number}/commits")
    Observable<Pageable<CommitModel>> getPullRequestCommits(@Path("owner") String owner, @Path("repo") String repo,
                                                            @Path("number") long number,
                                                            @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/{number}/files")
    Observable<Pageable<RepoFilesModel>> getPullRequestFiles(@Path("owner") String owner, @Path("repo") String repo,
                                                             @Path("number") long number,
                                                             @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/{number}/merge")
    Observable<Response<Boolean>> hasPullRequestBeenMerged(@Path("owner") String owner, @Path("repo") String repo,
                                                           @Path("number") long number);

    @PATCH("repos/{owner}/{repo}/pulls/{number}")
    Observable<PullRequestModel> editPullRequest(@Path("owner") String owner, @Path("repo") String repo,
                                                 @Path("number") int number,
                                                 @Body IssueRequestModel issue);

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    Observable<PullRequestModel> editIssue(@Path("owner") String owner, @Path("repo") String repo,
                                           @Path("number") int number,
                                           @Body IssueRequestModel issue);

    @POST("repos/{owner}/{repo}/issues/{number}/assignees")
    Observable<PullRequestModel> putAssignees(@Path("owner") String owner, @Path("repo") String repo,
                                        @Path("number") int number, @Body AssigneesRequestModel body);
}
