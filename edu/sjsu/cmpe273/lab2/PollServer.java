
/**
 * Created by aishwarya on 3/18/15.
 */

package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class PollServer {

    private static final Logger logger = Logger.getLogger(PollServer.class.getName());

    /* Server port information */
    private int port = 50051;
    private ServerImpl server;

    private void start() throws Exception {
        server = NettyServerBuilder.forPort(port)
                .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
                .build().start();
        logger.info("Server started, listening on : " + port );
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                PollServer.this.stop();
                System.err.println("*** server shut down");
            }
        });

    }

    private void stop(){
        if(server != null){
            server.shutdown();
        }
    }

    /* Main method will launch the server */
    public static void main(String[] args) throws Exception{
        final PollServer server = new PollServer();
        server.start();
    }

    private class PollServiceImpl implements PollServiceGrpc.PollService{
        @Override
        public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver){
	logger.info("Moderator Id : " + req.getModeratorId());

            //final AtomicLong poll_id= new AtomicLong();
            final AtomicInteger counter = new AtomicInteger(98345678);
            String poll_id = Integer.toHexString(counter.getAndIncrement());
            PollResponse reply = PollResponse.newBuilder().setId(poll_id).build();
            responseObserver.onValue(reply);
            responseObserver.onCompleted();

        }
    }

}


