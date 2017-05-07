package architecture_o.network.base;

import java.io.IOException;
import java.nio.Buffer;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;


/**
 * Created by Herbert on 2015/9/7.
 */
public class ProgressRequestBody extends RequestBody {

    protected RequestBody delegate;
    protected ProgressListener listener;

    protected CountingSink countingSink;

    public ProgressRequestBody(RequestBody delegate, ProgressListener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(okio.Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            if (listener != null)
                listener.onRequestProgress(bytesWritten, contentLength());
        }

    }

    public interface ProgressListener {
        void onRequestProgress(long bytesWritten, long contentLength);
    }
}